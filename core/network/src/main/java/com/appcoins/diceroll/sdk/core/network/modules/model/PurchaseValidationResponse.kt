package com.appcoins.diceroll.sdk.core.network.modules.model

import com.google.gson.annotations.SerializedName

data class PurchaseValidationResponse(
    @SerializedName("resource") val resourceResponse: ResourceResponse?
)

data class ResourceResponse(
    @SerializedName("kind") val kind: String?,
    @SerializedName("purchaseTimeMillis") val purchaseTimeMillis: Long?,
    @SerializedName("purchaseState") val purchaseState: Int?,
    @SerializedName("consumptionState") val consumptionState: Int?,
    @SerializedName("developerPayload") val developerPayload: String?,
    @SerializedName("orderId") val orderId: String?,
    @SerializedName("acknowledgementState") val acknowledgementState: Int?,
    @SerializedName("purchaseToken") val purchaseToken: String?,
    @SerializedName("productId") val productId: String?,
    @SerializedName("regionCode") val regionCode: String?
)
