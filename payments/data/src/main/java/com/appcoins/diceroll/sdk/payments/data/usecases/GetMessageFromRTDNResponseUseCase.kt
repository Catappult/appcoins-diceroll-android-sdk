package com.appcoins.diceroll.sdk.payments.data.usecases

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

class GetMessageFromRTDNResponseUseCase @Inject constructor() {

    operator fun invoke(message: String, onRemoveSubscription: (String) -> Unit): String? {
        try {
            val jsonObject = JSONObject(message)
            return when (jsonObject.optString("sku")) {
                "attempts" -> processAttemptsPurchaseUpdate(jsonObject)
                "golden_dice" -> processGoldenDiceSubscriptionUpdate(
                    jsonObject,
                    onRemoveSubscription
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

    private fun processGoldenDiceSubscriptionUpdate(
        jsonObject: JSONObject,
        onRemoveSubscription: (String) -> Unit
    ): String? {
        val status = jsonObject.optString("status")
        return when {
            status.equals("expired", true) -> {
                CoroutineScope(Dispatchers.IO).launch {
                    onRemoveSubscription("golden_dice")
                }
                "Your subscription to the Golden Dice has expired."
            }

            status.equals("refunded", true) -> {
                CoroutineScope(Dispatchers.IO).launch {
                    onRemoveSubscription("golden_dice")
                }
                "Your subscription to the Golden Dice was refunded."
            }

            else -> {
                Log.i(LOG_TAG, "Status is not important for the Attempts purchase.")
                null
            }
        }
    }

    private companion object {
        const val LOG_TAG = "GetMessageFromRTDNResponseUseCase"
    }
}
