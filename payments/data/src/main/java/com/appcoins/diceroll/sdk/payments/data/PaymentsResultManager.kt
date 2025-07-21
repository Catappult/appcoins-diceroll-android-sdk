package com.appcoins.diceroll.sdk.payments.data

import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetGoldenDicePremiumStatusUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetGoldenDiceStatusUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetPrepaidDiceStatusUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetRecurringDiscountDiceStatusUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetSingleDiscountDiceStatusUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetTrialDiceStatusUseCase
import com.appcoins.diceroll.sdk.payments.data.models.InternalPurchase
import com.appcoins.diceroll.sdk.payments.data.models.Item.Attempts
import com.appcoins.diceroll.sdk.payments.data.models.Item.GoldDice
import com.appcoins.diceroll.sdk.payments.data.models.Item.GoldDicePremium
import com.appcoins.diceroll.sdk.payments.data.models.Item.NonConsumableAttempts
import com.appcoins.diceroll.sdk.payments.data.models.Item.PrepaidDice
import com.appcoins.diceroll.sdk.payments.data.models.Item.RecurringDiscountDice
import com.appcoins.diceroll.sdk.payments.data.models.Item.SingleDiscountDice
import com.appcoins.diceroll.sdk.payments.data.models.Item.TrialDice
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessExpiredGoldenDicePremiumPurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessExpiredGoldenDicePurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessExpiredPrepaidDicePurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessExpiredRecurringDiscountDicePurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessExpiredSingleDiscountDicePurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessExpiredTrialDicePurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessSuccessfulAttemptsPurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessSuccessfulGoldenDicePremiumPurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessSuccessfulGoldenDicePurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessSuccessfulPrepaidDicePurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessSuccessfulRecurringDiscountDicePurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessSuccessfulSingleDiscountDicePurchaseUseCase
import com.appcoins.diceroll.sdk.payments.data.usecases.ProcessSuccessfulTrialDicePurchaseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class PaymentsResultManager @Inject constructor(
    private val processSuccessfulAttemptsPurchaseUseCase: ProcessSuccessfulAttemptsPurchaseUseCase,
    private val processSuccessfulGoldenDicePurchaseUseCase: ProcessSuccessfulGoldenDicePurchaseUseCase,
    private val processExpiredGoldenDicePurchaseUseCase: ProcessExpiredGoldenDicePurchaseUseCase,
    private val processSuccessfulGoldenDicePremiumPurchaseUseCase: ProcessSuccessfulGoldenDicePremiumPurchaseUseCase,
    private val processExpiredGoldenDicePremiumPurchaseUseCase: ProcessExpiredGoldenDicePremiumPurchaseUseCase,
    private val processSuccessfulTrialDicePurchaseUseCase: ProcessSuccessfulTrialDicePurchaseUseCase,
    private val processExpiredTrialDicePurchaseUseCase: ProcessExpiredTrialDicePurchaseUseCase,
    private val processSuccessfulPrepaidDicePurchaseUseCase: ProcessSuccessfulPrepaidDicePurchaseUseCase,
    private val processExpiredPrepaidDicePurchaseUseCase: ProcessExpiredPrepaidDicePurchaseUseCase,
    private val processSuccessfulSingleDiscountDicePurchaseUseCase: ProcessSuccessfulSingleDiscountDicePurchaseUseCase,
    private val processExpiredSingleDiscountDicePurchaseUseCase: ProcessExpiredSingleDiscountDicePurchaseUseCase,
    private val processSuccessfulRecurringDiscountDicePurchaseUseCase: ProcessSuccessfulRecurringDiscountDicePurchaseUseCase,
    private val processExpiredRecurringDiscountDicePurchaseUseCase: ProcessExpiredRecurringDiscountDicePurchaseUseCase,
    private val getGoldenDiceStatusUseCase: GetGoldenDiceStatusUseCase,
    private val getGoldenDicePremiumStatusUseCase: GetGoldenDicePremiumStatusUseCase,
    private val getTrialDiceStatusUseCase: GetTrialDiceStatusUseCase,
    private val getPrepaidDiceStatusUseCase: GetPrepaidDiceStatusUseCase,
    private val getSingleDiscountDiceStatusUseCase: GetSingleDiscountDiceStatusUseCase,
    private val getRecurringDiscountDiceStatusUseCase: GetRecurringDiscountDiceStatusUseCase,
) {
    fun processSuccessfulResult(internalPurchase: InternalPurchase) {
        CoroutineScope(Dispatchers.IO).launch {
            when (internalPurchase.sku) {
                Attempts.sku -> processSuccessfulAttemptsPurchaseUseCase(Attempts)
                NonConsumableAttempts.sku ->
                    processSuccessfulAttemptsPurchaseUseCase(NonConsumableAttempts)

                GoldDice.sku -> processSuccessfulGoldenDicePurchaseUseCase()
                GoldDicePremium.sku -> processSuccessfulGoldenDicePremiumPurchaseUseCase()
                TrialDice.sku -> processSuccessfulTrialDicePurchaseUseCase()
                PrepaidDice.sku -> processSuccessfulPrepaidDicePurchaseUseCase()
                SingleDiscountDice.sku -> processSuccessfulSingleDiscountDicePurchaseUseCase()
                RecurringDiscountDice.sku -> processSuccessfulRecurringDiscountDicePurchaseUseCase()
            }
        }
    }

    fun processExpiredSubscriptions(listSkus: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (getGoldenDiceStatusUseCase.invoke().firstOrNull() == true) {
                if (listSkus.firstOrNull { it == GoldDice.sku } == null) {
                    processExpiredGoldenDicePurchaseUseCase()
                }
            }
            if (getGoldenDicePremiumStatusUseCase.invoke().firstOrNull() == true) {
                if (listSkus.firstOrNull { it == GoldDicePremium.sku } == null) {
                    processExpiredGoldenDicePremiumPurchaseUseCase()
                }
            }
            if (getTrialDiceStatusUseCase.invoke().firstOrNull() == true) {
                if (listSkus.firstOrNull { it == TrialDice.sku } == null) {
                    processExpiredTrialDicePurchaseUseCase()
                }
            }
            if (getPrepaidDiceStatusUseCase.invoke().firstOrNull() == true) {
                if (listSkus.firstOrNull { it == PrepaidDice.sku } == null) {
                    processExpiredPrepaidDicePurchaseUseCase()
                }
            }
            if (getSingleDiscountDiceStatusUseCase.invoke().firstOrNull() == true) {
                if (listSkus.firstOrNull { it == SingleDiscountDice.sku } == null) {
                    processExpiredSingleDiscountDicePurchaseUseCase()
                }
            }
            if (getRecurringDiscountDiceStatusUseCase.invoke().firstOrNull() == true) {
                if (listSkus.firstOrNull { it == RecurringDiscountDice.sku } == null) {
                    processExpiredRecurringDiscountDicePurchaseUseCase()
                }
            }
        }
    }

    fun removeExpiredSubscription(sku: String) {
        processExpiredSubscriptions(listOf(sku))
    }
}
