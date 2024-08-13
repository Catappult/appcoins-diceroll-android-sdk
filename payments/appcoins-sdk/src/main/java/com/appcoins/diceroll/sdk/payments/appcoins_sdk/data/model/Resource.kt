package com.appcoins.diceroll.sdk.payments.appcoins_sdk.data.model

data class Resource(
    val kind: String? = null,
    val purchaseTimeMillis: Long? = null,
    val purchaseState: Int? = null,
    val consumptionState: Int? = null,
    val developerPayload: String? = null,
    val orderId: String? = null,
    val acknowledgementState: Int? = null,
    val purchaseToken: String? = null,
    val productId: String? = null,
    val regionCode: String? = null,
)