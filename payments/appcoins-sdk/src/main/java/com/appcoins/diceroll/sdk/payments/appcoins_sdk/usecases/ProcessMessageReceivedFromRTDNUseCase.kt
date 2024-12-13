package com.appcoins.diceroll.sdk.payments.appcoins_sdk.usecases

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.SdkManager.Companion.LOG_TAG
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

class ProcessMessageReceivedFromRTDNUseCase @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    operator fun invoke(message: String) {
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
                "golden_dice" -> processGoldenDiceSubscriptionUpdate(jsonObject)
                else -> "Unknown SKU received. $sku"
            }
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "Failed to parse message from RTDN.", ex)
        }

        return "Couldn't parse the message from RTDN."
    }

    private fun processAttemptsPurchaseUpdate(jsonObject: JSONObject): String? =
        if (jsonObject.optString("status") == "VOIDED") {
            "Your purchase for more Attempts was voided."
        } else {
            Log.i(LOG_TAG, "Status is not important for the Attempts purchase.")
            null
        }

    private fun processGoldenDiceSubscriptionUpdate(jsonObject: JSONObject): String? =
        if (jsonObject.optString("status") == "VOIDED") {
            "Your subscription for the Golden Dice was voided."
        } else {
            Log.i(LOG_TAG, "Status is not important for the GoldenDice purchase.")
            null
        }
}