package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.NON_CONSUMABLE_ATTEMPTS_NUMBER
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.AddAttemptsUseCase
import com.appcoins.diceroll.sdk.payments.data.models.Item
import com.appcoins.diceroll.sdk.payments.data.models.Item.Attempts
import com.appcoins.diceroll.sdk.payments.data.models.Item.NonConsumableAttempts
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentSuccess
import com.appcoins.diceroll.sdk.payments.data.streams.PurchaseStateStream
import javax.inject.Inject

class ProcessSuccessfulAttemptsPurchaseUseCase @Inject constructor(
    private val addAttemptsUseCase: AddAttemptsUseCase
) {

    suspend operator fun invoke(item: Item.ConsumableItem) {
        PurchaseStateStream.publish(PaymentSuccess(item))
        when (item) {
            Attempts -> addAttemptsUseCase()
            NonConsumableAttempts -> addAttemptsUseCase(NON_CONSUMABLE_ATTEMPTS_NUMBER)
        }
    }
}
