package com.appcoins.diceroll.sdk.payments.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
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
import com.appcoins.diceroll.sdk.feature.payments.data.Skus
import com.appcoins.diceroll.sdk.payments.billing.respository.PurchaseValidatorRepository
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode.ERROR
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode.ITEM_UNAVAILABLE
import com.appcoins.diceroll.sdk.payments.data.models.InternalSkuDetails
import com.appcoins.diceroll.sdk.payments.data.models.InternalSkuType
import com.appcoins.diceroll.sdk.payments.data.models.Item
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentError
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentLoading
import com.appcoins.diceroll.sdk.payments.data.streams.PurchaseStateStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Manages the Google Play Billing SDK integration.
 *
 * This class initializes the Google Billing Client, sets up
 * listeners for billing events, and provides methods to interact
 * with the billing service.
 *
 * It serves as a wrapper around the Google SDK to handle all the
 * necessary setup and provide callbacks to the app for billing events
 * in order to simplify the call for it.
 *
 */
interface SdkManager {
    /**
     * The Google Billing Client instance.
     */
    val billingClient: BillingClient

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
     * Process the result of a Successful Purchase
     */
    fun processSuccessfulPurchase(purchase: Purchase)

    /**
     * Process the expired Subscriptions
     */
    fun processExpiredPurchases(purchases: List<Purchase>)

    /**
     * Listener for Google Billing Client state changes.
     *
     * This listener handles events related to the connection state
     * of the Google billing client and has two methods to act on connection and
     * disconnection events.
     *
     * @param billingResult The response code from the billing client
     */
    val billingClientStateListener: BillingClientStateListener
        get() =
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    when (billingResult.responseCode) {
                        BillingResponseCode.OK -> {
                            Log.i(
                                LOG_TAG,
                                "BillingClientStateListener: Google SDK Setup successful. Querying inventory."
                            )
                            _connectionState.value = true
                            setupRTDNListener()
                            queryPurchases()
                            queryActiveSubscriptions()
                            queryInappProducts(ArrayList(Skus.INAPPS))
                            querySubsProducts(ArrayList(Skus.SUBS))
                        }

                        else -> {
                            Log.i(
                                LOG_TAG,
                                "BillingClientStateListener: Problem setting up Google SDK: ${billingResult.responseCode}"
                            )
                            _connectionState.value = false
                            _attemptsPrice.value = null
                            _purchasableItems.clear()
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.i(LOG_TAG, "BillingClientStateListener: Google SDK Disconnected")
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
     * @param billingResult The [BillingResult] from the billing client
     * @param purchases The list of Purchase objects with the purchase data
     */
    val purchasesUpdatedListener: PurchasesUpdatedListener
        get() = PurchasesUpdatedListener { billingResult: BillingResult, purchases: MutableList<Purchase>? ->
            when (billingResult.responseCode) {
                BillingResponseCode.OK -> {
                    if (!purchases.isNullOrEmpty()) {
                        for (purchase in purchases) {
                            _purchases.add(purchase)
                            Log.i(
                                LOG_TAG, "PurchasesUpdatedListener: purchase data:" +
                                    "\nsku: ${purchase.products.first()}" +
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

                            val product = purchase.products.first()
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
                        "PurchasesUpdatedListener: response ${billingResult.responseCode} response message: ${billingResult.debugMessage}"
                    )
                }
            }
        }

    /**
     * Listener for handling consume purchase responses.
     *
     * This listener receives the response code and purchase token
     * after consuming a purchase with the billing client.
     *
     * It can be used to determine if the consumption was successful.
     *
     * @param billingResult The [BillingResult] from consuming purchase
     * @param purchaseToken The token of the consumed purchase
     */
    val consumeResponseListener: ConsumeResponseListener
        get() =
            ConsumeResponseListener { billingResult, purchaseToken ->
                Log.d(
                    LOG_TAG,
                    "ConsumeResponseListener: Consumption finished. Purchase: $purchaseToken, result: $billingResult"
                )
            }

    /**
     * Listener for handling acknowledge purchase responses.
     *
     * This listener receives the response code and purchase token
     * after consuming a purchase with the billing client.
     *
     * It can be used to determine if the consumption was successful.
     *
     * @param billingResult The [BillingResult] from consuming purchase
     * @param purchaseToken The token of the consumed purchase
     */
    val acknowledgePurchaseResponseListener: AcknowledgePurchaseResponseListener
        get() =
            AcknowledgePurchaseResponseListener { billingResult ->
                Log.d(
                    LOG_TAG,
                    "AcknowledgePurchaseResponseListener: Acknowledge finished. result: $billingResult"
                )
            }

    /**
     * Starts the payment flow for the given SKU.
     *
     * @param sku The SKU identifier for the in-app product.
     * @param developerPayload A developer-defined string that will be returned with the purchase data.
     *
     * This will launch the billing flow. The result will be delivered
     * via the PurchasesUpdatedListener callback.
     */
    fun startPayment(activity: Activity, sku: String, skuType: String, developerPayload: String?) {
        CoroutineScope(Job()).launch {
            PurchaseStateStream.eventFlow.emit(PaymentLoading)
        }

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
                .apply {
                    if (skuType == ProductType.SUBS) {
                        val offerToken = getOfferTokenFromProduct(productDetails)
                        setOfferToken(
                            productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: ""
                        )
                    }
                }
                .build()
        )

        val billingFlowParams =
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .apply {
                    developerPayload?.let {
                        setObfuscatedAccountId(it)
                    }
                }.build()


        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    fun launchAppUpdateDialog(context: Context) {
        //cab.launchAppUpdateDialog(context)
    }

    private fun validateAndConsumePurchase(purchase: Purchase, skipValidation: Boolean = false) {
        CoroutineScope(Job()).launch {
            val product = purchase.products.first()
            val purchaseToken = purchase.purchaseToken
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
        skipValidation: Boolean = false
    ) {
        CoroutineScope(Job()).launch {
            val product = purchase.products.first()
            val purchaseToken = purchase.purchaseToken
            val isPurchaseValid =
                skipValidation || BuildConfig.DEBUG || isPurchaseValid(product, purchaseToken)

            if (isPurchaseValid) {
                Log.i(LOG_TAG, "Purchase verified successfully from Server side.")
                billingClient.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchaseToken).build(),
                    acknowledgePurchaseResponseListener
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
            if (billingResult.responseCode == BillingResponseCode.OK) {
                for (purchase in purchases) {
                    _purchases.add(purchase)
                    validateAndConsumePurchase(purchase)
                }
            }
        }
    }

    private fun queryActiveSubscriptions() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(ProductType.SUBS).build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                for (purchase in purchases) {
                    _purchases.add(purchase)
                    validateAndAcknowledgePurchase(purchase)
                }
                processExpiredPurchases(purchases)
            }
        }
    }

    private fun queryInappProducts(skuList: List<String>) {
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

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsResult ->
            processProductDetailsResult(
                billingResult,
                productDetailsResult,
                ProductType.INAPP
            )
        }
    }

    private fun querySubsProducts(skuList: List<String>) {
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
            processProductDetailsResult(
                billingResult,
                details,
                ProductType.SUBS
            )
        }
    }

    /**
     * Listener for SKU details responses.
     *
     * Called when the requested SKU details are retrieved from the billing client.
     *
     * The SKU details list contains the details about each SKU.
     * This can be used to show SKU information in the app UI.
     *
     * @param billingResult The [BillingResult] from the billing client
     * @param productDetailsResult QueryProductDetailsResult containing the products list and unfecthed products.
     * @param skuType Type of Product
     */
    private fun processProductDetailsResult(
        billingResult: BillingResult,
        products: List<ProductDetails>,
        skuType: String
    ) {
        Log.d(
            LOG_TAG,
            "processProductDetailsResult: item response ${billingResult.responseCode}, response message: ${billingResult.debugMessage}"
        )
        if (billingResult.responseCode == 0) {
            for (productDetails in products) {
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
                    if (productDetails.productId == Skus.ATTEMPTS) {
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
        val nonConsumableProducts = listOf(Skus.NON_CONSUMABLE_ATTEMPTS)
        return nonConsumableProducts.contains(product)
    }

    private fun getOfferTokenFromProduct(productDetails: ProductDetails): String {
        return when (productDetails.productId) {
            Skus.TRIAL_DICE -> productDetails.subscriptionOfferDetails?.firstOrNull { it.offerId == "trial-offer" }?.offerToken
            Skus.SINGLE_DISCOUNT_DICE -> productDetails.subscriptionOfferDetails?.firstOrNull { it.offerId == "single-discount-offer" }?.offerToken
            Skus.RECURRING_DISCOUNT_DICE -> productDetails.subscriptionOfferDetails?.firstOrNull { it.offerId == "recurring-discount-offer" }?.offerToken
            else -> null
        } ?: productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
        ?: ""
    }

    companion object {
        const val LOG_TAG = "SdkManager"
    }
}
