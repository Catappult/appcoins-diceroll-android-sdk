package com.appcoins.diceroll.sdk.payments.data.streams

import com.appcoins.diceroll.sdk.core.utils.EventBusInterface
import kotlinx.coroutines.flow.MutableSharedFlow

object PurchaseStateStream : EventBusInterface {

    override var eventFlow = MutableSharedFlow<Any>()
}
