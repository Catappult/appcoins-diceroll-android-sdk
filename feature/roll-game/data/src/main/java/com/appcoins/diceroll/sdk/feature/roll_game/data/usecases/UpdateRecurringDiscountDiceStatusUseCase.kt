package com.appcoins.diceroll.sdk.feature.roll_game.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.RecurringDiscountDiceDataSource
import javax.inject.Inject

class UpdateRecurringDiscountDiceStatusUseCase @Inject constructor(private val datastore: RecurringDiscountDiceDataSource) {

    suspend operator fun invoke(active: Boolean) = datastore.saveRecurringDiscountDiceStatus(active)
}
