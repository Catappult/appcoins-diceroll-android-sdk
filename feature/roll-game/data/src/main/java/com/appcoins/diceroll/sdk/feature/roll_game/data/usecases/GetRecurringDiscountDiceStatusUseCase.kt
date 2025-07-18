package com.appcoins.diceroll.sdk.feature.roll_game.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.RecurringDiscountDiceDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecurringDiscountDiceStatusUseCase @Inject constructor(private val datastore: RecurringDiscountDiceDataSource) {

    operator fun invoke(): Flow<Boolean> = datastore.getRecurringDiscountDiceStatus()
}
