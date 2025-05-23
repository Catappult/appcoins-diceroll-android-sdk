package com.appcoins.diceroll.sdk.payments.data.models

import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode as ResponseCode

interface PaymentState {
    data object PaymentIdle : PaymentState
    data object PaymentLoading : PaymentState
    data class PaymentSuccess(val item: Item) : PaymentState
    data class PaymentError(val item: Item?, val responseCode: ResponseCode) : PaymentState
}
