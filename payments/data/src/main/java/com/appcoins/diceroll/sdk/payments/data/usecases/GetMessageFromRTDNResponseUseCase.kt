package com.appcoins.diceroll.sdk.payments.data.usecases

import android.util.Log
import com.appcoins.diceroll.sdk.feature.payments.data.Skus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

class GetMessageFromRTDNResponseUseCase @Inject constructor() {

    operator fun invoke(message: String, onRemoveSubscription: (String) -> Unit): String? {
        try {
            val jsonObject = JSONObject(message)
            val sku = jsonObject.optString("sku")
            return when (sku) {
                Skus.ATTEMPTS -> processAttemptsPurchaseUpdate(jsonObject)
                Skus.GOLDEN_DICE, Skus.TRIAL_DICE, Skus.PREPAID_DICE -> processSubscriptionUpdate(
                    jsonObject,
                    sku,
                    onRemoveSubscription,
                )

                else -> null
            }
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "Failed to parse message from RTDN.", ex)
        }

        Log.e(LOG_TAG, "Couldn't parse the message from RTDN.")
        return null
    }

    private fun processAttemptsPurchaseUpdate(jsonObject: JSONObject): String? {
        val status = jsonObject.optString("status")
        return when {
            status.equals("refunded", true) ->
                "Your purchase for more Attempts was refunded."

            else -> {
                Log.i(LOG_TAG, "Status is not important for the Attempts purchase.")
                null
            }
        }
    }

    private fun processSubscriptionUpdate(
        jsonObject: JSONObject,
        sku: String,
        onRemoveSubscription: (String) -> Unit
    ): String? {
        val status = jsonObject.optString("status")
        return when {
            status.equals("expired", true) -> {
                CoroutineScope(Dispatchers.IO).launch {
                    onRemoveSubscription(sku)
                }
                "Your subscription to the $sku has expired."
            }

            status.equals("refunded", true) -> {
                CoroutineScope(Dispatchers.IO).launch {
                    onRemoveSubscription(sku)
                }
                "Your subscription to the $sku was refunded."
            }

            else -> {
                Log.i(LOG_TAG, "Status is not important for the $sku purchase.")
                null
            }
        }
    }

    private companion object {
        const val LOG_TAG = "GetMessageFromRTDNResponseUseCase"
    }
}
