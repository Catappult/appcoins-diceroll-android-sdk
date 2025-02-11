package com.appcoins.diceroll.sdk.core.utils.payments.models

interface PaymentState {
    data object PaymentIdle : PaymentState
    data object PaymentLoading : PaymentState
    data class PaymentSuccess(val itemId: String) : PaymentState
    data class PaymentError(val itemId: String, val cause: String) : PaymentState
}
