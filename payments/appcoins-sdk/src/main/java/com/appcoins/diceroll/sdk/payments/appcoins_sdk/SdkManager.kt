package com.appcoins.diceroll.sdk.payments.appcoins_sdk

import android.app.Activity
import android.content.Context
import android.util.Log
import com.appcoins.diceroll.sdk.core.utils.PurchaseResultStream
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.data.respository.PurchaseValidatorRepository
import com.appcoins.sdk.billing.AppcoinsBillingClient
import com.appcoins.sdk.billing.BillingFlowParams
import com.appcoins.sdk.billing.Purchase
import com.appcoins.sdk.billing.PurchasesUpdatedListener
import com.appcoins.sdk.billing.ResponseCode
import com.appcoins.sdk.billing.SkuDetailsParams
import com.appcoins.sdk.billing.listeners.AppCoinsBillingStateListener
import com.appcoins.sdk.billing.listeners.ConsumeResponseListener
import com.appcoins.sdk.billing.listeners.PurchaseResponse
import com.appcoins.sdk.billing.listeners.SkuDetailsResponseListener
import com.appcoins.sdk.billing.types.SkuType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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
interface SdkManager {
    /**
     * The AppCoins billing client instance.
     */
    val cab: AppcoinsBillingClient

    val _connectionState: MutableStateFlow<Boolean>

    val _attemptsPrice: MutableStateFlow<String?>

    val _purchases: ArrayList<Purchase>

    val _purchaseValidatorRepository: PurchaseValidatorRepository

    /**
     * Listener for AppCoins billing client state changes.
     *
     * This listener handles events related to the connection state
     * of the AppCoins billing client and has two methods to act on connection and
     * disconnection events.
     *
     * @param responseCode The response code from the billing client
     */
    val appCoinsBillingStateListener: AppCoinsBillingStateListener
        get() =
            object : AppCoinsBillingStateListener {
                override fun onBillingSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        ResponseCode.OK.value -> {
                            Log.d(
                                LOG_TAG,
                                "AppCoinsBillingStateListener: AppCoins SDK Setup successful. Querying inventory."
                            )
                            _connectionState.value = true
                            queryPurchases()
                            queryInapps(ArrayList(listOf("attempts")))
                        }

                        else -> {
                            Log.d(
                                LOG_TAG,
                                "AppCoinsBillingStateListener: Problem setting up AppCoins SDK: ${responseCode.toResponseCode()}"
                            )
                            _connectionState.value = false
                            _attemptsPrice.value = null
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.d(LOG_TAG, "AppCoinsBillingStateListener: AppCoins SDK Disconnected")
                    _connectionState.value = false
                    _attemptsPrice.value = null
                }
            }

    /**
     * Listener that gets called when purchases are updated.
     *
     * This listener handles the response codes and purchase data
     * from the billing client after a purchase flow completes.
     *
     * It will be called with the response code and list of purchases.
     * Based on the response code, it can process the purchases or
     * handle errors.
     *
     * @param responseCode The response code from the billing client
     * @param purchases The list of Purchase objects with the purchase data
     */
    val purchasesUpdatedListener: PurchasesUpdatedListener
        get() = PurchasesUpdatedListener { responseCode: Int, purchases: List<Purchase> ->
            when (responseCode) {
                ResponseCode.OK.value -> {
                    for (purchase in purchases) {
                        _purchases.add(purchase)
                        Log.i(
                            LOG_TAG, "PurchasesUpdatedListener: purchase data:" +
                                "\nsku: ${purchase.sku}" +
                                "\nitemType: ${purchase.itemType}" +
                                "\npackageName: ${purchase.packageName}" +
                                "\ndeveloperPayload: ${purchase.developerPayload}" +
                                "\npurchaseState: ${purchase.purchaseState}" +
                                "\npurchaseTime: ${purchase.purchaseTime}" +
                                "\ntoken: ${purchase.token}" +
                                "\norderId: ${purchase.orderId}" +
                                "\nsignature: ${purchase.signature}" +
                                "\noriginalJson: ${purchase.originalJson}" +
                                "\nisAutoRenewing: ${purchase.isAutoRenewing}"
                        )
                        validateAndConsumePurchase(purchase)
                    }
                }

                else -> {
                    CoroutineScope(Job()).launch {
                        PurchaseResultStream.publish(PurchaseResponse(responseCode, purchases))
                    }
                    Log.d(
                        LOG_TAG,
                        "PurchasesUpdatedListener: response ${responseCode.toResponseCode()}"
                    )
                }
            }
        }

    /**
     * Listener for handling consume purchase responses.
     *
     * This listener receives the response code and purchase token
     * after consuming a purchase with the AppCoins billing client.
     *
     * It can be used to determine if the consumption was successful.
     *
     * @param responseCode The response code from consuming purchase
     * @param purchaseToken The token of the consumed purchase
     */
    val consumeResponseListener: ConsumeResponseListener
        get() =
            ConsumeResponseListener { responseCode, purchaseToken ->
                updateUIWithConsumeResult(responseCode, purchaseToken)
                Log.d(
                    LOG_TAG,
                    "ConsumeResponseListener: Consumption finished. Purchase: $purchaseToken, result: $responseCode"
                )
            }

    /**
     * Listener for SKU details responses.
     *
     * Called when the requested SKU details are retrieved from the
     * AppCoins billing client.
     *
     * The SKU details list contains the details about each SKU.
     * This can be used to show SKU information in the app UI.
     *
     * @param responseCode The response code from the billing client
     * @param skuDetailsList List of SkuDetails objects
     */
    val skuDetailsResponseListener: SkuDetailsResponseListener
        get() =
            SkuDetailsResponseListener { responseCode, skuDetailsList ->
                for (sku in skuDetailsList) {
                    Log.d(
                        LOG_TAG,
                        "SkuDetailsResponseListener: item response ${responseCode.toResponseCode()}, sku $sku"
                    )
                    if (sku.sku == "attempts") {
                        _attemptsPrice.value = sku.price
                    }
                    // You can add these details to a list in order to update
                    // UI or use it in any other way
                }
            }

    /**
     * Method to start the Setup of the SDK.
     */
    fun setupSdkConnection(context: Context)

    /**
     * Starts the payment flow for the given SKU.
     *
     * @param sku The SKU identifier for the in-app product.
     * @param developerPayload A developer-defined string that will be returned with the purchase data.
     *
     * This will launch the Google Play billing flow. The result will be delivered
     * via the PurchasesUpdatedListener callback.
     */
    fun startPayment(context: Context, sku: String, developerPayload: String) {
        val billingFlowParams = BillingFlowParams(
            sku,
            SkuType.inapp.toString(),
            "orderId=" + System.currentTimeMillis(),
            developerPayload,
            "BDS"
        )

        CoroutineScope(Job()).launch {
            cab.launchBillingFlow(context as Activity, billingFlowParams)
        }
    }

    fun launchAppUpdateDialog(context: Context) {
        cab.launchAppUpdateDialog(context)
    }

    private fun validateAndConsumePurchase(purchase: Purchase, skipValidation: Boolean = false) {
        CoroutineScope(Job()).launch {
            val isPurchaseValid =
                skipValidation ||
                    BuildConfig.DEBUG ||
                    isPurchaseValid(purchase.sku, purchase.token ?: "")

            if (isPurchaseValid) {
                Log.i(LOG_TAG, "Purchase verified successfully from Server side.")
                cab.consumeAsync(purchase.token, consumeResponseListener)
            } else {
                CoroutineScope(Job()).launch {
                    PurchaseResultStream.publish(
                        PurchaseResponse(
                            ResponseCode.ERROR.value,
                            listOf(purchase)
                        )
                    )
                }
                Log.e(LOG_TAG, "There was an error verifying the Purchase on Server side.")
            }
        }
    }

    private fun updateUIWithConsumeResult(responseCode: Int, purchaseToken: String) {
        CoroutineScope(Job()).launch {
            val purchase = _purchases.firstOrNull { it.token == purchaseToken }
            if (purchase != null) {
                PurchaseResultStream.publish(PurchaseResponse(responseCode, listOf(purchase)))
            }
        }
    }

    private fun queryPurchases() {
        CoroutineScope(Dispatchers.IO).launch {
            val purchasesResult = cab.queryPurchases(SkuType.inapp.toString())
            val purchases = purchasesResult.purchases
            for (purchase in purchases) {
                _purchases.add(purchase)
                validateAndConsumePurchase(purchase)
            }
        }
    }

    private fun queryInapps(skuList: List<String>) {
        cab.querySkuDetailsAsync(
            SkuDetailsParams().apply {
                itemType = SkuType.inapp.toString()
                moreItemSkus = skuList
            },
            skuDetailsResponseListener
        )
    }

    private suspend fun isPurchaseValid(sku: String, token: String): Boolean =
        _purchaseValidatorRepository
            .isPurchaseValid(sku, token)
            .getOrDefault(false)

    private companion object {
        const val LOG_TAG = "SdkManager"
    }
}
