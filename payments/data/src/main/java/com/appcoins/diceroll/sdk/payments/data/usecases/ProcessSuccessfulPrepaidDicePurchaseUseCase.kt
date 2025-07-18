package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SubscriptionsDataSource
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.PREPAID_DICE
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetPrepaidDiceStatusUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdatePrepaidDiceStatusUseCase
import com.appcoins.diceroll.sdk.payments.data.models.Item.PrepaidDice
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentSuccess
import com.appcoins.diceroll.sdk.payments.data.streams.PurchaseStateStream
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ProcessSuccessfulPrepaidDicePurchaseUseCase @Inject constructor(
    private val getPrepaidDiceStatusUseCase: GetPrepaidDiceStatusUseCase,
    private val updatePrepaidDiceStatusUseCase: UpdatePrepaidDiceStatusUseCase,
    private val subscriptionsDataSource: SubscriptionsDataSource,
) {

    suspend operator fun invoke() {
        if (getPrepaidDiceStatusUseCase().firstOrNull() != true) {
            PurchaseStateStream.publish(PaymentSuccess(PrepaidDice))
            updatePrepaidDiceStatusUseCase(true)
            subscriptionsDataSource.saveSelectedSubscription(PREPAID_DICE)
        }
    }
}
