package com.appcoins.diceroll.sdk.payments.billing.respository

import android.content.Context
import com.appcoins.diceroll.sdk.core.network.modules.api.PurchaseValidatorApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PurchaseValidatorRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val purchaseValidatorApi: PurchaseValidatorApi,
) {

    suspend fun isPurchaseValid(
        sku: String,
        purchaseToken: String
    ): Result<Boolean> =
        Result.success(true)
}
