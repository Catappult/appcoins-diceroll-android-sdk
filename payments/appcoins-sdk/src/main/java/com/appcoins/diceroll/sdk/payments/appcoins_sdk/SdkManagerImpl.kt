package com.appcoins.diceroll.sdk.payments.appcoins_sdk

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.appcoins.diceroll.sdk.core.network.clients.RTDNWebSocketClient
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetGoldenDiceStatusUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdateGoldenDiceStatusUseCase
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.SdkManager.Companion.LOG_TAG
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.data.respository.PurchaseValidatorRepository
import com.appcoins.sdk.billing.AppcoinsBillingClient
import com.appcoins.sdk.billing.Purchase
import com.appcoins.sdk.billing.helpers.CatapultBillingAppCoinsFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.json.JSONObject
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
    val getGoldenDiceStatusUseCase: GetGoldenDiceStatusUseCase,
    val updateGoldenDiceStatusUseCase: UpdateGoldenDiceStatusUseCase,
    private val webSocketClient: RTDNWebSocketClient
) : SdkManager {

    override lateinit var cab: AppcoinsBillingClient

    override val _connectionState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val _goldDiceSubscriptionActive: MutableStateFlow<Boolean?> = MutableStateFlow(null)

    override val _attemptsPrice: MutableStateFlow<String?> = MutableStateFlow(null)

    override val _goldDicePrice: MutableStateFlow<String?> = MutableStateFlow(null)

    override val _purchases: ArrayList<Purchase> = ArrayList()

    override val _purchaseValidatorRepository: PurchaseValidatorRepository =
        purchaseValidatorRepository

    private val BASE_64_ENCODED_PUBLIC_KEY = BuildConfig.CATAPPULT_PUBLIC_KEY

    private var isRTDNConnectionEstablished = false

    override fun setupSdkConnection(context: Context) {
        cab =
            CatapultBillingAppCoinsFactory.BuildAppcoinsBilling(
                context,
                BASE_64_ENCODED_PUBLIC_KEY,
                purchasesUpdatedListener
            )
        cab.startConnection(appCoinsBillingStateListener)

        CoroutineScope(Dispatchers.IO).launch {
            _goldDiceSubscriptionActive.value = getGoldenDiceStatusUseCase().firstOrNull()
        }
    }

    override fun processGoldenDiceSubscription(active: Boolean) {
        _goldDiceSubscriptionActive.value = active
        CoroutineScope(Dispatchers.IO).launch {
            updateGoldenDiceStatusUseCase(active)
        }
    }

    override fun setupRTDNListener() {
        if (!isRTDNConnectionEstablished) {
            webSocketClient.connectToRTDNApi(rtdnListener)
            isRTDNConnectionEstablished = true
        }
    }

    /**
     * Listener for RTDN.
     */
    private val rtdnListener: (String) -> Unit
        get() = { message ->
            Log.i(LOG_TAG, "Received RTDN message: $message")
            processMessageFromRTDN(message)?.let { messageToShow ->
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, messageToShow, Toast.LENGTH_LONG).show()
                }
            }
        }

    private fun processMessageFromRTDN(message: String): String? {
        try {
            val jsonObject = JSONObject(message)
            return when (val sku = jsonObject.optString("sku")) {
                "attempts" -> processAttemptsPurchaseUpdate(jsonObject)
                "daily_dice" -> processGoldenDiceSubscriptionUpdate(jsonObject)
                else -> "Unknown SKU received. $sku"
            }
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "Failed to parse message from RTDN.", ex)
        }

        return "Couldn't parse the message from RTDN."
    }

    private fun processAttemptsPurchaseUpdate(jsonObject: JSONObject): String? =
        if (jsonObject.optString("status") == "EXPIRED") {
            "Your purchase for more Attempts was voided."
        } else {
            Log.i(LOG_TAG, "Status is not important for the Attempts purchase.")
            null
        }

    private fun processGoldenDiceSubscriptionUpdate(jsonObject: JSONObject): String? =
        if (jsonObject.optString("status") == "EXPIRED") {
            CoroutineScope(Dispatchers.IO).launch {
                processGoldenDiceSubscription(false)
            }
            "Your subscription for the Golden Dice was voided."
        } else {
            Log.i(LOG_TAG, "Status is not important for the GoldenDice purchase.")
            null
        }
}