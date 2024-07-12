package com.appcoins.diceroll.sdk.core.utils

import kotlinx.coroutines.flow.MutableSharedFlow

object PurchaseResultStream : EventBusInterface {

    override var eventFlow = MutableSharedFlow<Any>()
}
