package com.appcoins.diceroll.sdk.core.network.clients.rtdn

import android.util.Log
import com.appcoins.diceroll.sdk.core.network.clients.WebSocketClient
import com.appcoins.diceroll.sdk.core.utils.rtdnWebSocketUrl
import com.appcoins.diceroll.sdk.feature.settings.data.usecases.GetUserUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import javax.inject.Inject

class RTDNWebSocketClient @Inject constructor(
    val getUserUseCase: GetUserUseCase,
) : WebSocketClient() {

    private lateinit var rtdnMessageListener: RTDNMessageListener
    private var exponentialFactor = 1L

    fun connectToRTDNApi(rtdnMessageListener: RTDNMessageListener) {
        this.rtdnMessageListener = rtdnMessageListener
        val uuid = getUserUseCase().uuid
        Log.i(TAG, "connectToRTDNApi: connecting to $rtdnWebSocketUrl$uuid")
        connect(rtdnWebSocketUrl + uuid)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.i(TAG, "onMessage: received message from RTDN Api -> $text")
        rtdnMessageListener.onMessageReceived(text)
        super.onMessage(webSocket, text)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.i(TAG, "onOpen: connection is completed, message:${response.message}")
        super.onOpen(webSocket, response)
        exponentialFactor = 1
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.i(TAG, "onClosing: connection is being closed: reason:$reason")
        super.onClosing(webSocket, code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.i(TAG, "onClosed: connection is closed: reason:$reason")
        super.onClosed(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.i(TAG, "onFailure: there was a failure, message:${response?.message}")
        super.onFailure(webSocket, t, response)
        reconnect(webSocket)
    }

    private fun reconnect(webSocket: WebSocket) {
        webSocket.cancel()
        webSocket.close(1000, "Internally closed.")
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000 * exponentialFactor)
            if (exponentialFactor < 60) {
                exponentialFactor *= 2
            }
            connectToRTDNApi(rtdnMessageListener)
        }
    }

    private companion object {
        const val TAG = "RTDNWebSocketClient"
    }
}
