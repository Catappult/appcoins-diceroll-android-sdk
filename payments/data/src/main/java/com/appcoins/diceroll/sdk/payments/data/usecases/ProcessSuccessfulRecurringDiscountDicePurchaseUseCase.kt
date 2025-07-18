package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SubscriptionsDataSource
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.RECURRING_DISCOUNT_DICE
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetRecurringDiscountDiceStatusUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdateRecurringDiscountDiceStatusUseCase
import com.appcoins.diceroll.sdk.payments.data.models.Item.RecurringDiscountDice
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentSuccess
import com.appcoins.diceroll.sdk.payments.data.streams.PurchaseStateStream
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ProcessSuccessfulRecurringDiscountDicePurchaseUseCase @Inject constructor(
    private val getRecurringDiscountDiceStatusUseCase: GetRecurringDiscountDiceStatusUseCase,
    private val updateRecurringDiscountDiceStatusUseCase: UpdateRecurringDiscountDiceStatusUseCase,
    private val subscriptionsDataSource: SubscriptionsDataSource,
) {

    suspend operator fun invoke() {
        if (getRecurringDiscountDiceStatusUseCase().firstOrNull() != true) {
            PurchaseStateStream.publish(PaymentSuccess(RecurringDiscountDice))
            updateRecurringDiscountDiceStatusUseCase(true)
            subscriptionsDataSource.saveSelectedSubscription(RECURRING_DISCOUNT_DICE)
        }
    }
}
