package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SubscriptionsDataSource
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.SINGLE_DISCOUNT_DICE
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetSingleDiscountDiceStatusUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdateSingleDiscountDiceStatusUseCase
import com.appcoins.diceroll.sdk.payments.data.models.Item.SingleDiscountDice
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentSuccess
import com.appcoins.diceroll.sdk.payments.data.streams.PurchaseStateStream
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ProcessSuccessfulSingleDiscountDicePurchaseUseCase @Inject constructor(
    private val getSingleDiscountDiceStatusUseCase: GetSingleDiscountDiceStatusUseCase,
    private val updateSingleDiscountDiceStatusUseCase: UpdateSingleDiscountDiceStatusUseCase,
    private val subscriptionsDataSource: SubscriptionsDataSource,
) {

    suspend operator fun invoke() {
        if (getSingleDiscountDiceStatusUseCase().firstOrNull() != true) {
            PurchaseStateStream.publish(PaymentSuccess(SingleDiscountDice))
            updateSingleDiscountDiceStatusUseCase(true)
            subscriptionsDataSource.saveSelectedSubscription(SINGLE_DISCOUNT_DICE)
        }
    }
}
