package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SubscriptionsDataSource
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.TRIAL_DICE
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdateTrialDiceStatusUseCase
import javax.inject.Inject

class ProcessExpiredTrialDicePurchaseUseCase @Inject constructor(
    private val updateTrialDiceStatusUseCase: UpdateTrialDiceStatusUseCase,
    private val subscriptionsDataSource: SubscriptionsDataSource,
) {

    suspend operator fun invoke() {
        updateTrialDiceStatusUseCase(false)
        subscriptionsDataSource.processExpiredSubscription(TRIAL_DICE)
    }
}
