package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SubscriptionsDataSource
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.RECURRING_DISCOUNT_DICE
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdateRecurringDiscountDiceStatusUseCase
import javax.inject.Inject

class ProcessExpiredRecurringDiscountDicePurchaseUseCase @Inject constructor(
    private val updateRecurringDiscountDiceStatusUseCase: UpdateRecurringDiscountDiceStatusUseCase,
    private val subscriptionsDataSource: SubscriptionsDataSource,
) {

    suspend operator fun invoke() {
        updateRecurringDiscountDiceStatusUseCase(false)
        subscriptionsDataSource.processExpiredSubscription(RECURRING_DISCOUNT_DICE)
    }
}
