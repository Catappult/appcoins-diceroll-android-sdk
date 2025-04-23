package com.appcoins.diceroll.sdk.payments.data

import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetGoldenDiceStatusUseCase
import com.appcoins.diceroll.sdk.payments.data.models.InternalPurchase
import com.appcoins.diceroll.sdk.payments.data.models.Item.Attempts
import com.appcoins.diceroll.sdk.payments.data.models.Item.GoldDice
import com.appcoins.diceroll.sdk.payments.data.models.Item.NonConsumableAttempts
import com.appcoins.diceroll.sdk.payments.data.models.Item.TrialDice
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessExpiredGoldenDicePurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessSuccessfulAttemptsPurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessSuccessfulGoldenDicePurchaseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class PaymentsResultManager @Inject constructor(
    private val processSuccessfulGoldenDicePurchaseUseCase: ProcessSuccessfulGoldenDicePurchaseUseCase,
    private val processSuccessfulAttemptsPurchaseUseCase: ProcessSuccessfulAttemptsPurchaseUseCase,
    private val processExpiredGoldenDicePurchaseUseCase: ProcessExpiredGoldenDicePurchaseUseCase,
    private val getGoldenDiceStatusUseCase: GetGoldenDiceStatusUseCase,
) {
    fun processSuccessfulResult(internalPurchase: InternalPurchase) {
        CoroutineScope(Dispatchers.IO).launch {
            when (internalPurchase.sku) {
                Attempts.sku -> processSuccessfulAttemptsPurchaseUseCase(Attempts)
                NonConsumableAttempts.sku ->
                    processSuccessfulAttemptsPurchaseUseCase(NonConsumableAttempts)

                GoldDice.sku, TrialDice.sku -> processSuccessfulGoldenDicePurchaseUseCase()
            }
        }
    }

    fun processExpiredSubscriptions(listSkus: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (getGoldenDiceStatusUseCase.invoke().firstOrNull() == true) {
                if (listSkus.firstOrNull { it == GoldDice.sku || it == TrialDice.sku } == null) {
                    processExpiredGoldenDicePurchaseUseCase()
                }
            }
        }
    }

    fun removeExpiredSubscription(sku: String) {
        processExpiredSubscriptions(listOf(sku))
    }
}
