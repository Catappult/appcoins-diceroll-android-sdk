package com.appcoins.diceroll.sdk.core.network.clients

import android.util.Log
import com.appcoins.diceroll.sdk.core.utils.rtdnWebSocketUrl
import com.appcoins.diceroll.sdk.feature.settings.data.usecases.GetUserUseCase
import okhttp3.Response
import okhttp3.WebSocket
import javax.inject.Inject

class RTDNWebSocketClient @Inject constructor(
    val getUserUseCase: GetUserUseCase,
) : WebSocketClient() {

    private lateinit var onMessageCallback: (String) -> Unit

    fun connectToRTDNApi(onMessageCallback: (String) -> Unit) {
        this.onMessageCallback = onMessageCallback
        val uuid = getUserUseCase().uuid
        Log.i(TAG, "connectToRTDNApi: connecting to $rtdnWebSocketUrl$uuid")
        connect(rtdnWebSocketUrl + uuid)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.i(TAG, "onMessage: received message from RTDN Api -> $text")
        onMessageCallback(text)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.i(TAG, "onOpen: connection is completed")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.i(TAG, "onClosed: connection is closed")
    }

    private companion object {
        const val TAG = "RTDNWebSocketClient"
    }
}