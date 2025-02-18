package com.appcoins.diceroll.sdk.payments.google

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryPurchasesAsync
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode.ERROR
import com.appcoins.diceroll.sdk.payments.data.models.InternalSkuDetails
import com.appcoins.diceroll.sdk.payments.data.models.InternalSkuType
import com.appcoins.diceroll.sdk.payments.data.models.Item
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentError
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentLoading
import com.appcoins.diceroll.sdk.payments.data.streams.PurchaseStateStream
import com.appcoins.diceroll.sdk.payments.google.data.respository.PurchaseValidatorRepository
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
    val cab: BillingClient

    val _connectionState: MutableStateFlow<Boolean>

    val _attemptsPrice: MutableStateFlow<String?>

    val _goldDicePrice: MutableStateFlow<String?>

    val _purchasableItems: MutableList<InternalSkuDetails>

    val _purchases: ArrayList<Purchase>

    val _purchaseValidatorRepository: PurchaseValidatorRepository

    val myItems: MutableList<ProductDetails>

    /**
     * Method to start the Setup of the SDK.
     */
    fun setupSdkConnection(context: Context)

    /**
     * Method to start the Listener of the RTDN Api.
     */
    fun setupRTDNListener()

    /**
     * Process the result of a Purchase of type golden_dice
     */
    fun processSuccessfulPurchase(purchase: Purchase)

    /**
     * Process the result of a Purchase of type golden_dice
     */
    fun processExpiredPurchases(purchases: List<Purchase>)

    /**
     * Listener for AppCoins billing client state changes.
     *
     * This listener handles events related to the connection state
     * of the AppCoins billing client and has two methods to act on connection and
     * disconnection events.
     *
     * @param responseCode The response code from the billing client
     */
    val billingClientStateListener: BillingClientStateListener
        get() =
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    when (billingResult.responseCode) {
                        BillingResponseCode.OK -> {
                            Log.d(
                                LOG_TAG,
                                "AppCoinsBillingStateListener: AppCoins SDK Setup successful. Querying inventory."
                            )
                            _connectionState.value = true
                            setupRTDNListener()
                            queryPurchases()
                            queryActiveSubscriptions()
                            queryInappsSkus(ArrayList(listOf("attempts")))
                            querySubsSkus(ArrayList(listOf("golden_dice")))
                        }

                        else -> {
                            Log.d(
                                LOG_TAG,
                                "AppCoinsBillingStateListener: Problem setting up AppCoins SDK: ${billingResult.responseCode}"
                            )
                            _connectionState.value = false
                            _attemptsPrice.value = null
                            _goldDicePrice.value = null
                            _purchasableItems.clear()
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.d(LOG_TAG, "AppCoinsBillingStateListener: AppCoins SDK Disconnected")
                    _connectionState.value = false
                    _attemptsPrice.value = null
                    _goldDicePrice.value = null
                    _purchasableItems.clear()
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
        get() = PurchasesUpdatedListener { billingResult: BillingResult, purchases: MutableList<Purchase>? ->
            when (billingResult.responseCode) {
                BillingResponseCode.OK -> {
                    if (purchases != null) {
                        for (purchase in purchases) {
                            _purchases.add(purchase)
                            Log.i(
                                LOG_TAG, "PurchasesUpdatedListener: purchase data:" +
                                    "\nsku: ${purchase.products.first()}" +
                                    "\nitemType: ${purchase}" +
                                    "\npackageName: ${purchase.packageName}" +
                                    "\ndeveloperPayload: ${purchase.developerPayload}" +
                                    "\npurchaseState: ${purchase.purchaseState}" +
                                    "\npurchaseTime: ${purchase.purchaseTime}" +
                                    "\ntoken: ${purchase.purchaseToken}" +
                                    "\norderId: ${purchase.orderId}" +
                                    "\nsignature: ${purchase.signature}" +
                                    "\noriginalJson: ${purchase.originalJson}" +
                                    "\nisAutoRenewing: ${purchase.isAutoRenewing}"
                            )
                            validateAndConsumePurchase(
                                purchase,
                                true
                            )
                        }
                    } else {
                        CoroutineScope(Job()).launch {
                            PurchaseStateStream.publish(
                                PaymentError(
                                    null,
                                    InternalResponseCode.entries.find { it.value == billingResult.responseCode }
                                        ?: ERROR)
                            )
                        }
                    }
                }

                else -> {
                    CoroutineScope(Job()).launch {
                        PurchaseStateStream.publish(
                            PaymentError(
                                null,
                                InternalResponseCode.entries.find { it.value == billingResult.responseCode }
                                    ?: ERROR)
                        )
                    }
                    Log.d(
                        LOG_TAG,
                        "PurchasesUpdatedListener: response ${billingResult.responseCode}"
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
    fun processSkuDetailsResult(
        billingResult: BillingResult,
        productDetailsList: List<ProductDetails>,
        skuType: String
    ) {
        if (billingResult.responseCode == 0) {
            for (productDetails in productDetailsList) {
                Log.d(
                    LOG_TAG,
                    "processSkuDetailsResult: item response ${billingResult.responseCode}, productDetails $productDetails"
                )
                if (_purchasableItems.find { it.sku == productDetails.productId } == null) {
                    _purchasableItems.add(
                        InternalSkuDetails(
                            productDetails.productId,
                            InternalSkuType.entries.first {
                                productDetails.productType.equals(it.value, true)
                            }
                        )
                    )
                    myItems.add(productDetails)
                    if (productDetails.productId == "attempts") {
                        _attemptsPrice.value =
                            productDetails.oneTimePurchaseOfferDetails?.formattedPrice
                    }
                    if (productDetails.productId == "golden_dice") {
                        _goldDicePrice.value =
                            productDetails.subscriptionOfferDetails?.first()!!.pricingPhases.pricingPhaseList.first().formattedPrice
                    }
                    // You can add these details to a list in order to update
                    // UI or use it in any other way
                }
            }
        }
    }

    /**
     * Starts the payment flow for the given SKU.
     *
     * @param sku The SKU identifier for the in-app product.
     * @param developerPayload A developer-defined string that will be returned with the purchase data.
     *
     * This will launch the Google Play billing flow. The result will be delivered
     * via the PurchasesUpdatedListener callback.
     */
    fun startPayment(
        activity: Activity,
        sku: String,
        skuType: String? = null,
        developerPayload: String?
    ) {
        CoroutineScope(Job()).launch {
            PurchaseStateStream.eventFlow.emit(PaymentLoading)
        }
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(myItems.first { it.productId == sku })
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .setObfuscatedProfileId(developerPayload ?: "")
            .build()

        cab.launchBillingFlow(activity, billingFlowParams)
    }

    fun launchAppUpdateDialog(context: Context) {
        //cab.launchAppUpdateDialog(context)
    }

    private fun validateAndConsumePurchase(
        purchase: Purchase,
        skipValidation: Boolean = false
    ) {
        CoroutineScope(Job()).launch {
            val isPurchaseValid =
                skipValidation ||
                    BuildConfig.DEBUG ||
                    isPurchaseValid(purchase.products.first(), purchase.purchaseToken)

            if (isPurchaseValid) {
                Log.i(LOG_TAG, "Purchase verified successfully from Server side.")
                cab.consumeAsync(
                    ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build(),
                    consumeResponseListener
                )
                processSuccessfulPurchase(purchase)
            } else {
                CoroutineScope(Job()).launch {
                    PurchaseStateStream.publish(
                        PaymentError(Item.fromSku(purchase.products.first()), ERROR)
                    )
                }
                Log.e(LOG_TAG, "There was an error verifying the Purchase on Server side.")
            }
        }
    }

    private fun queryPurchases() {
        CoroutineScope(Dispatchers.IO).launch {
            cab.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(ProductType.INAPP).build()
            )
        }
    }

    private fun queryActiveSubscriptions() {
        CoroutineScope(Dispatchers.IO).launch {
            cab.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(ProductType.SUBS).build()
            )
        }
    }

    private fun queryInappsSkus(skuList: List<String>) {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    skuList.map {
                        Product.newBuilder()
                            .setProductId(it)
                            .setProductType(ProductType.INAPP)
                            .build()
                    }
                )
                .build()

        cab.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, details ->
            processSkuDetailsResult(
                billingResult,
                details,
                ProductType.INAPP
            )
        }
    }

    private fun querySubsSkus(skuList: List<String>) {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    skuList.map {
                        Product.newBuilder()
                            .setProductId(it)
                            .setProductType(ProductType.INAPP)
                            .build()
                    }
                )
                .build()

        cab.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, details ->
            processSkuDetailsResult(
                billingResult,
                details,
                ProductType.SUBS
            )
        }
    }

    private suspend fun isPurchaseValid(sku: String, token: String): Boolean =
        _purchaseValidatorRepository
            .isPurchaseValid(sku, token)
            .getOrDefault(false)

    companion object {
        const val LOG_TAG = "SdkManager"
    }
}
