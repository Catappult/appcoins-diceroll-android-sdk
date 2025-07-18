package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SubscriptionsDataSource
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.PREPAID_DICE
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdatePrepaidDiceStatusUseCase
import javax.inject.Inject

class ProcessExpiredPrepaidDicePurchaseUseCase @Inject constructor(
    private val updatePrepaidDiceStatusUseCase: UpdatePrepaidDiceStatusUseCase,
    private val subscriptionsDataSource: SubscriptionsDataSource,
) {

    suspend operator fun invoke() {
        updatePrepaidDiceStatusUseCase(false)
        subscriptionsDataSource.processExpiredSubscription(PREPAID_DICE)
    }
}
