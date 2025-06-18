package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SubscriptionsDataSource
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.GOLDEN_DICE
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdateGoldenDiceStatusUseCase
import javax.inject.Inject

class ProcessExpiredGoldenDicePurchaseUseCase @Inject constructor(
    private val updateGoldenDiceStatusUseCase: UpdateGoldenDiceStatusUseCase,
    private val subscriptionsDataSource: SubscriptionsDataSource,
) {

    suspend operator fun invoke() {
        updateGoldenDiceStatusUseCase(false)
        subscriptionsDataSource.processExpiredSubscription(GOLDEN_DICE)
    }
}
