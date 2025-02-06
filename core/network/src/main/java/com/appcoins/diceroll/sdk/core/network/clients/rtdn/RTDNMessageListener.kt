package com.appcoins.diceroll.sdk.core.network.clients.rtdn

interface RTDNMessageListener {
    fun onMessageReceived(message: String)
}
