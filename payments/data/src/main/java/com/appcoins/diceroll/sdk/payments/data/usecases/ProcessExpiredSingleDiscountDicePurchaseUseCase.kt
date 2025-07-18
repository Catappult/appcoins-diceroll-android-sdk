package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SubscriptionsDataSource
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.SINGLE_DISCOUNT_DICE
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdateSingleDiscountDiceStatusUseCase
import javax.inject.Inject

class ProcessExpiredSingleDiscountDicePurchaseUseCase @Inject constructor(
    private val updateSingleDiscountDiceStatusUseCase: UpdateSingleDiscountDiceStatusUseCase,
    private val subscriptionsDataSource: SubscriptionsDataSource,
) {

    suspend operator fun invoke() {
        updateSingleDiscountDiceStatusUseCase(false)
        subscriptionsDataSource.processExpiredSubscription(SINGLE_DISCOUNT_DICE)
    }
}
