package com.appcoins.diceroll.sdk.feature.roll_game.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SingleDiscountDiceDataSource
import javax.inject.Inject

class UpdateSingleDiscountDiceStatusUseCase @Inject constructor(private val datastore: SingleDiscountDiceDataSource) {

    suspend operator fun invoke(active: Boolean) = datastore.saveSingleDiscountDiceStatus(active)
}
