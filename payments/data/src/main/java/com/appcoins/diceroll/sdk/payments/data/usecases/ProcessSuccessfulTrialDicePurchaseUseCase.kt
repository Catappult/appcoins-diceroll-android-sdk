package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SubscriptionsDataSource
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.TRIAL_DICE
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetTrialDiceStatusUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdateTrialDiceStatusUseCase
import com.appcoins.diceroll.sdk.payments.data.models.Item.TrialDice
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentSuccess
import com.appcoins.diceroll.sdk.payments.data.streams.PurchaseStateStream
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ProcessSuccessfulTrialDicePurchaseUseCase @Inject constructor(
    private val getTrialDiceStatusUseCase: GetTrialDiceStatusUseCase,
    private val updateTrialDiceStatusUseCase: UpdateTrialDiceStatusUseCase,
    private val subscriptionsDataSource: SubscriptionsDataSource,
) {

    suspend operator fun invoke() {
        if (getTrialDiceStatusUseCase().firstOrNull() != true) {
            PurchaseStateStream.publish(PaymentSuccess(TrialDice))
            updateTrialDiceStatusUseCase(true)
            subscriptionsDataSource.saveSelectedSubscription(TRIAL_DICE)
        }
    }
}
