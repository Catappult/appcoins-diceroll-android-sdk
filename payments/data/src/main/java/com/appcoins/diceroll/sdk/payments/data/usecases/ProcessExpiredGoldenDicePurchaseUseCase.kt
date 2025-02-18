package com.appcoins.diceroll.sdk.payments.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.UpdateGoldenDiceStatusUseCase
import javax.inject.Inject

class ProcessExpiredGoldenDicePurchaseUseCase @Inject constructor(
    private val updateGoldenDiceStatusUseCase: UpdateGoldenDiceStatusUseCase
) {

    suspend operator fun invoke() {
        updateGoldenDiceStatusUseCase(false)
    }
}
