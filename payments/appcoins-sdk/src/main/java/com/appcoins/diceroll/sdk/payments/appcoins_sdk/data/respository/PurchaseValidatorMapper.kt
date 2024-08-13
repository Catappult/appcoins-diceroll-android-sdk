package com.appcoins.diceroll.sdk.payments.appcoins_sdk.data.respository

import com.appcoins.diceroll.sdk.core.network.modules.model.PurchaseValidationResponse
import com.appcoins.diceroll.sdk.core.network.modules.model.ResourceResponse
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.data.model.PurchaseValidation
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.data.model.Resource

fun PurchaseValidationResponse.toPurchaseValidation(): PurchaseValidation =
    PurchaseValidation(
        resource = resourceResponse?.toResource()
    )

fun ResourceResponse.toResource(): Resource =
    Resource(
        kind = kind,
        purchaseTimeMillis = purchaseTimeMillis,
        purchaseState = purchaseState,
        consumptionState = consumptionState,
        developerPayload = developerPayload,
        orderId = orderId,
        acknowledgementState = acknowledgementState,
        purchaseToken = purchaseToken,
        productId = productId,
        regionCode = regionCode,
    )
