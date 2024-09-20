package com.appcoins.diceroll.sdk.payments.appcoins_sdk

import android.content.Context
import com.appcoins.diceroll.sdk.core.utils.ActivityResultStream
import com.appcoins.diceroll.sdk.core.utils.listen
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.data.respository.PurchaseValidatorRepository
import com.appcoins.sdk.billing.AppcoinsBillingClient
import com.appcoins.sdk.billing.Purchase
import com.appcoins.sdk.billing.helpers.CatapultBillingAppCoinsFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the AppCoins SDK integration for in-app billing.
 *
 * This class initializes the AppCoins billing client, sets up
 * listeners for billing events, and provides methods to interact
 * with the billing service.
 *
 * It serves as a wrapper around the AppCoins SDK to handle all the
 * necessary setup and provide callbacks to the app for billing events
 * in order to simplify the call for it.
 *
 */
class SdkManagerImpl @Inject constructor(
    purchaseValidatorRepository: PurchaseValidatorRepository
) : SdkManager {

    override lateinit var cab: AppcoinsBillingClient

    override val _connectionState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val _attemptsPrice: MutableStateFlow<String?> = MutableStateFlow(null)

    override val _purchases: ArrayList<Purchase> = ArrayList()

    override val _purchaseValidatorRepository: PurchaseValidatorRepository =
        purchaseValidatorRepository

    private val BASE_64_ENCODED_PUBLIC_KEY = BuildConfig.CATAPPULT_PUBLIC_KEY

    override fun setupSdkConnection(context: Context) {
        cab =
            CatapultBillingAppCoinsFactory.BuildAppcoinsBilling(
                context,
                BASE_64_ENCODED_PUBLIC_KEY,
                purchasesUpdatedListener
            )
        cab.startConnection(appCoinsBillingStateListener)
        sdkResultHandle()
    }

    private fun sdkResultHandle() {
        CoroutineScope(Job()).launch {
            ActivityResultStream.listen<SdkResult>().collect {
                cab.onActivityResult(it.requestCode, it.resultCode, it.data)
            }
        }
    }
}