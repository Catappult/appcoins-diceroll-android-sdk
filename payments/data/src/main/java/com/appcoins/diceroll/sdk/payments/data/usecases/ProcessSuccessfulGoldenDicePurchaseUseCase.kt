package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetGoldenDiceStatusUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdateGoldenDiceStatusUseCase
import com.appcoins.diceroll.sdk.payments.data.models.Item.GoldDice
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentSuccess
import com.appcoins.diceroll.sdk.payments.data.streams.PurchaseStateStream
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ProcessSuccessfulGoldenDicePurchaseUseCase @Inject constructor(
    private val getGoldenDiceStatusUseCase: GetGoldenDiceStatusUseCase,
    private val updateGoldenDiceStatusUseCase: UpdateGoldenDiceStatusUseCase,
) {

    suspend operator fun invoke() {
        if (getGoldenDiceStatusUseCase().firstOrNull() != true) {
            PurchaseStateStream.publish(PaymentSuccess(GoldDice))
            updateGoldenDiceStatusUseCase(true)
        }
    }
}
