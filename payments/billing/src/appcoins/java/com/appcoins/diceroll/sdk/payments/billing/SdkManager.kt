package com.appcoins.diceroll.sdk.payments.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.appcoins.diceroll.sdk.payments.billing.data.respository.PurchaseValidatorRepository
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode.ERROR
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode.ITEM_UNAVAILABLE
import com.appcoins.diceroll.sdk.payments.data.models.InternalSkuDetails
import com.appcoins.diceroll.sdk.payments.data.models.InternalSkuType
import com.appcoins.diceroll.sdk.payments.data.models.Item
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentError
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentLoading
import com.appcoins.diceroll.sdk.payments.data.streams.PurchaseStateStream
import com.appcoins.sdk.billing.AppcoinsBillingClient
import com.appcoins.sdk.billing.BillingFlowParams
import com.appcoins.sdk.billing.BillingResult
import com.appcoins.sdk.billing.CatapultAppcoinsBilling.ProductType
import com.appcoins.sdk.billing.ConsumeParams
import com.appcoins.sdk.billing.FeatureType
import com.appcoins.sdk.billing.ProductDetails
import com.appcoins.sdk.billing.Purchase
import com.appcoins.sdk.billing.PurchasesUpdatedListener
import com.appcoins.sdk.billing.QueryProductDetailsParams
import com.appcoins.sdk.billing.QueryProductDetailsParams.Product
import com.appcoins.sdk.billing.QueryPurchasesParams
import com.appcoins.sdk.billing.listeners.AppCoinsBillingStateListener
import com.appcoins.sdk.billing.listeners.ConsumeResponseListener
import com.appcoins.sdk.billing.types.SkuType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode as ResponseCode

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
    val billingClient: AppcoinsBillingClient

    val _connectionState: MutableStateFlow<Boolean>

    val _attemptsPrice: MutableStateFlow<String?>

    val _purchasableItems: MutableList<InternalSkuDetails>

    val _purchases: ArrayList<Purchase>

    val _purchaseValidatorRepository: PurchaseValidatorRepository

    val _myItems: MutableList<ProductDetails>

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
                            setupRTDNListener()
                            queryPurchases()
                            queryActiveSubscriptions()
                            queryInappsSkus(ArrayList(Skus.INAPPS))
                            querySubsSkus(ArrayList(Skus.SUBS))
                        }

                        else -> {
                            Log.d(
                                LOG_TAG,
                                "AppCoinsBillingStateListener: Problem setting up AppCoins SDK: ${responseCode.toResponseCode()}"
                            )
                            _connectionState.value = false
                            _attemptsPrice.value = null
                            _purchasableItems.clear()
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.d(LOG_TAG, "AppCoinsBillingStateListener: AppCoins SDK Disconnected")
                    _connectionState.value = false
                    _attemptsPrice.value = null
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
        get() = PurchasesUpdatedListener { responseCode: Int, purchases: List<Purchase> ->
            when (responseCode) {
                ResponseCode.OK.value -> {
                    if (purchases.isNotEmpty()) {
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

                            val product = purchase.sku
                            if (isSubscriptionTypeProduct(product) || isNonConsumableProduct(product)) {
                                validateAndAcknowledgePurchase(purchase)
                            } else {
                                validateAndConsumePurchase(purchase)
                            }
                        }
                    } else {
                        CoroutineScope(Job()).launch {
                            PurchaseStateStream.publish(
                                PaymentError(
                                    null,
                                    ResponseCode.entries.find { it.value == responseCode } ?: ERROR)
                            )
                        }
                    }
                }

                else -> {
                    CoroutineScope(Job()).launch {
                        PurchaseStateStream.publish(
                            PaymentError(
                                null,
                                ResponseCode.entries.find { it.value == responseCode } ?: ERROR)
                        )
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
                Log.d(
                    LOG_TAG,
                    "ConsumeResponseListener: Consumption finished. Purchase: $purchaseToken, result: $responseCode"
                )
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
    fun startPayment(context: Context, sku: String, skuType: String, developerPayload: String?) {
        CoroutineScope(Job()).launch {
            PurchaseStateStream.eventFlow.emit(PaymentLoading)
        }

        val shouldStartFreeTrial = isFreeTrialSubscription(sku, skuType, developerPayload)

        val productDetails = _myItems.firstOrNull { it.productId == sku }

        if (productDetails == null) {
            CoroutineScope(Job()).launch {
                PurchaseStateStream.eventFlow.emit(PaymentError(null, ITEM_UNAVAILABLE))
            }
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams =
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .apply {
                    developerPayload?.let {
                        setDeveloperPayload(it)
                        setObfuscatedAccountId(it)
                    }
                    setFreeTrial(shouldStartFreeTrial)
                }.build()

        CoroutineScope(Job()).launch {
            billingClient.launchBillingFlow(context as Activity, billingFlowParams)
        }
    }

    fun launchAppUpdateDialog(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            if (billingClient.isAppUpdateAvailable) {
                billingClient.launchAppUpdateDialog(context)
            }
        }
    }

    private fun validateAndConsumePurchase(purchase: Purchase, skipValidation: Boolean = false) {
        CoroutineScope(Job()).launch {
            val product = purchase.sku
            val purchaseToken = purchase.token ?: ""
            val isPurchaseValid =
                skipValidation || BuildConfig.DEBUG || isPurchaseValid(product, purchaseToken)

            if (isPurchaseValid) {
                Log.i(LOG_TAG, "Purchase verified successfully from Server side.")
                billingClient.consumeAsync(
                    ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build(),
                    consumeResponseListener
                )
                processSuccessfulPurchase(purchase)
            } else {
                CoroutineScope(Job()).launch {
                    PurchaseStateStream.publish(PaymentError(Item.fromSku(product), ERROR))
                }
                Log.e(LOG_TAG, "There was an error verifying the Purchase on Server side.")
            }
        }
    }

    private fun validateAndAcknowledgePurchase(
        purchase: Purchase,
        skipValidation: Boolean = true
    ) {
        CoroutineScope(Job()).launch {
            val product = purchase.sku
            val purchaseToken = purchase.token ?: ""
            val isPurchaseValid =
                skipValidation || BuildConfig.DEBUG || isPurchaseValid(product, purchaseToken)

            if (isPurchaseValid) {
                Log.i(LOG_TAG, "Purchase verified successfully from Server side.")
                billingClient.consumeAsync(
                    ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build(),
                    consumeResponseListener
                )
                processSuccessfulPurchase(purchase)
            } else {
                CoroutineScope(Job()).launch {
                    PurchaseStateStream.publish(PaymentError(Item.fromSku(product), ERROR))
                }
                Log.e(LOG_TAG, "There was an error verifying the Purchase on Server side.")
            }
        }
    }

    private fun queryPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(ProductType.INAPP).build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == InternalResponseCode.OK.value) {
                for (purchase in purchases) {
                    _purchases.add(purchase)
                    validateAndConsumePurchase(purchase)
                }
            }
        }
    }

    private fun queryActiveSubscriptions() {
        CoroutineScope(Dispatchers.IO).launch {
            val purchasesResult = billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(ProductType.SUBS).build()
            )
            val purchases = purchasesResult.purchases
            for (purchase in purchases) {
                _purchases.add(purchase)
                validateAndAcknowledgePurchase(purchase)
            }
            processExpiredPurchases(purchases)
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

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, details ->
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
                            .setProductType(ProductType.SUBS)
                            .build()
                    }
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, details ->
            processSkuDetailsResult(
                billingResult,
                details,
                ProductType.SUBS
            )
        }
    }

    /**
     * Listener for SKU details responses.
     *
     * Called when the requested SKU details are retrieved from the
     * Google billing client.
     *
     * The SKU details list contains the details about each SKU.
     * This can be used to show SKU information in the app UI.
     *
     * @param billingResult The [BillingResult] from the billing client
     * @param productDetailsList List of ProductDetails objects
     * @param skuType Type of Product
     */
    private fun processSkuDetailsResult(
        billingResult: BillingResult,
        productDetailsList: List<ProductDetails>,
        skuType: String
    ) {
        Log.d(
            LOG_TAG,
            "processSkuDetailsResult: item response ${billingResult.responseCode}, response message: ${billingResult.debugMessage}"
        )
        if (billingResult.responseCode == 0) {
            for (productDetails in productDetailsList) {
                if (_purchasableItems.find { it.sku == productDetails.productId } == null) {
                    _purchasableItems.add(
                        InternalSkuDetails(
                            productDetails.productId,
                            InternalSkuType.entries.first {
                                skuType.equals(it.value, true)
                            },
                            productDetails.title,
                            getPriceFromProduct(productDetails, skuType)
                        )
                    )
                    _myItems.add(productDetails)
                    if (productDetails.productId == "attempts") {
                        _attemptsPrice.value =
                            productDetails.oneTimePurchaseOfferDetails?.formattedPrice
                    }
                }
            }
        }
    }

    private fun getPriceFromProduct(productDetails: ProductDetails, skuType: String): String {
        return if (skuType == ProductType.SUBS) {
            productDetails.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice
                ?: ""
        } else {
            productDetails.oneTimePurchaseOfferDetails?.formattedPrice ?: ""
        }
    }

    private suspend fun isPurchaseValid(sku: String, token: String): Boolean =
        _purchaseValidatorRepository
            .isPurchaseValid(sku, token)
            .getOrDefault(false)

    private fun isSubscriptionTypeProduct(product: String?): Boolean {
        return _myItems.firstOrNull { it.productId == product }?.productType == ProductType.SUBS
    }

    private fun isNonConsumableProduct(product: String?): Boolean {
        val nonConsumableProducts = listOf("non_consumable_attempts")
        return nonConsumableProducts.contains(product)
    }

    private fun isFreeTrialSubscription(
        sku: String,
        skuType: String,
        developerPayload: String?
    ): Boolean {
        // First verify if the Free Trial feature and Obfucasted Account Id parameter are available
        if (billingClient.isFeatureSupported(FeatureType.FREE_TRIALS) != 0) {
            return false
        }

        if (billingClient.isFeatureSupported(FeatureType.OBFUSCATED_ACCOUNT_ID) != 0) {
            return false
        }

        // Verify if the Sku Type is a Subscription
        if (skuType != SkuType.subs.toString()) {
            return false
        }

        return sku == "trial_dice"
    }

    companion object {
        const val LOG_TAG = "SdkManager"
    }
}
