package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SubscriptionsDataSource
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.GOLDEN_DICE_PREMIUM
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetGoldenDicePremiumStatusUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdateGoldenDicePremiumStatusUseCase
import com.appcoins.diceroll.sdk.payments.data.models.Item.GoldDicePremium
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentSuccess
import com.appcoins.diceroll.sdk.payments.data.streams.PurchaseStateStream
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ProcessSuccessfulGoldenDicePremiumPurchaseUseCase @Inject constructor(
    private val getGoldenDicePremiumStatusUseCase: GetGoldenDicePremiumStatusUseCase,
    private val updateGoldenDicePremiumStatusUseCase: UpdateGoldenDicePremiumStatusUseCase,
    private val subscriptionsDataSource: SubscriptionsDataSource,
) {

    suspend operator fun invoke() {
        if (getGoldenDicePremiumStatusUseCase().firstOrNull() != true) {
            PurchaseStateStream.publish(PaymentSuccess(GoldDicePremium))
            updateGoldenDicePremiumStatusUseCase(true)
            subscriptionsDataSource.saveSelectedSubscription(GOLDEN_DICE_PREMIUM)
        }
    }
}
