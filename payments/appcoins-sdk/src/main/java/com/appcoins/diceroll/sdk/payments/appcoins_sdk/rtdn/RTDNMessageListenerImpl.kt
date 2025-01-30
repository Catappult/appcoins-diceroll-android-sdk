package com.appcoins.diceroll.sdk.payments.appcoins_sdk.rtdn

import android.util.Log
import com.appcoins.diceroll.sdk.core.network.clients.rtdn.RTDNMessageListener
import com.appcoins.diceroll.sdk.core.ui.notifications.NotificationHandler
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.usecases.GetMessageFromRTNDResponseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RTDNMessageListenerImpl(
    private val notificationHandler: NotificationHandler,
    private val getMessageFromRTNDResponseUseCase: GetMessageFromRTNDResponseUseCase,
    private val onRemoveSubscription: (String) -> Unit
) : RTDNMessageListener {

    override fun onMessageReceived(message: String) {
        Log.i(TAG, "Received RTDN message: $message")
        getMessageFromRTNDResponseUseCase(message, onRemoveSubscription)?.let { messageToShow ->
            CoroutineScope(Dispatchers.Main).launch {
                notificationHandler.showPurchaseNotification(messageToShow)
            }
        }
    }

    private companion object {
        val TAG = RTDNMessageListenerImpl::class.simpleName
    }
}
