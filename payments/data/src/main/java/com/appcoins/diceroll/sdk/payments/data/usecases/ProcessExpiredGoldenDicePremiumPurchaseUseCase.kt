package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SubscriptionsDataSource
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.GOLDEN_DICE_PREMIUM
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdateGoldenDicePremiumStatusUseCase
import javax.inject.Inject

class ProcessExpiredGoldenDicePremiumPurchaseUseCase @Inject constructor(
    private val updateGoldenDicePremiumStatusUseCase: UpdateGoldenDicePremiumStatusUseCase,
    private val subscriptionsDataSource: SubscriptionsDataSource,
) {

    suspend operator fun invoke() {
        updateGoldenDicePremiumStatusUseCase(false)
        subscriptionsDataSource.processExpiredSubscription(GOLDEN_DICE_PREMIUM)
    }
}
