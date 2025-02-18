package com.appcoins.diceroll.sdk.payments.google

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.appcoins.diceroll.sdk.core.network.clients.rtdn.RTDNWebSocketClient
import com.appcoins.diceroll.sdk.core.ui.notifications.NotificationHandler
import com.appcoins.diceroll.sdk.payments.data.PaymentsResultManager
import com.appcoins.diceroll.sdk.payments.data.models.InternalSkuDetails
import com.appcoins.diceroll.sdk.payments.data.rtdn.RTDNMessageListenerImpl
import com.appcoins.diceroll.sdk.payments.data.usecases.GetMessageFromRTDNResponseUseCase
import com.appcoins.diceroll.sdk.payments.google.data.respository.PurchaseValidatorRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
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
    @ApplicationContext
    val context: Context,
    purchaseValidatorRepository: PurchaseValidatorRepository,
    getMessageFromRTDNResponseUseCase: GetMessageFromRTDNResponseUseCase,
    notificationHandler: NotificationHandler,
    private val webSocketClient: RTDNWebSocketClient,
    private val paymentsResultManager: PaymentsResultManager,
) : SdkManager {

    override lateinit var cab: BillingClient

    override val _connectionState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val _attemptsPrice: MutableStateFlow<String?> = MutableStateFlow(null)

    override val _goldDicePrice: MutableStateFlow<String?> = MutableStateFlow(null)

    override val _purchasableItems: MutableList<InternalSkuDetails> =
        mutableStateListOf()

    override val myItems: MutableList<ProductDetails> = mutableStateListOf()

    override val _purchases: ArrayList<Purchase> = ArrayList()

    override val _purchaseValidatorRepository: PurchaseValidatorRepository =
        purchaseValidatorRepository

    private var isRTDNConnectionEstablished = false

    /**
     * Listener for RTDN.
     */
    private val rtdnListener = RTDNMessageListenerImpl(
        notificationHandler,
        getMessageFromRTDNResponseUseCase,
        ::onRemoveSubscription
    )

    override fun setupSdkConnection(context: Context) {
        cab = BillingClient.newBuilder(context).setListener(purchasesUpdatedListener).build()
        cab.startConnection(billingClientStateListener)
    }

    override fun processSuccessfulPurchase(purchase: Purchase) {
        paymentsResultManager.processSuccessfulResult(
            com.appcoins.diceroll.sdk.payments.data.models.InternalPurchase(
                purchase.products.first()
            )
        )
    }

    override fun processExpiredPurchases(purchases: List<Purchase>) {
        paymentsResultManager.processExpiredSubscriptions(purchases.map { it.products.first() })
    }

    override fun setupRTDNListener() {
        if (!isRTDNConnectionEstablished) {
            webSocketClient.connectToRTDNApi(rtdnListener)
            isRTDNConnectionEstablished = true
        }
    }

    private fun onRemoveSubscription(sku: String) {
        paymentsResultManager.removeExpiredSubscription(sku)
    }
}
