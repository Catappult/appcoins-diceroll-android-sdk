package com.appcoins.diceroll.sdk.core.network.clients

import okhttp3.OkHttpClient.Builder
import okhttp3.Request
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

open class WebSocketClient : WebSocketListener() {

    open fun connect(url: String) {
        val client = Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(url)
            .build()
        client.newWebSocket(request, this)
    }
}