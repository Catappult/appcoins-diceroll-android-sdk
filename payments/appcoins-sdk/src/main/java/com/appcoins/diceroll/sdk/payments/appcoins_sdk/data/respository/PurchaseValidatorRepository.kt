package com.appcoins.diceroll.sdk.payments.appcoins_sdk.data.respository

import com.appcoins.diceroll.sdk.core.network.HttpCodes.isSuccess
import com.appcoins.diceroll.sdk.core.network.modules.api.PurchaseValidatorApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PurchaseValidatorRepository @Inject constructor(
    private val purchaseValidatorApi: PurchaseValidatorApi,
) {

    suspend fun isPurchaseValid(
        packageName: String,
        sku: String,
        purchaseToken: String
    ): Result<Boolean> =
        runCatching {
            withContext(Dispatchers.IO) {
                purchaseValidatorApi.getPurchaseValidationState(
                    packageName = packageName,
                    sku = sku,
                    purchaseToken = purchaseToken
                ).code().isSuccess()
            }
        }
}
