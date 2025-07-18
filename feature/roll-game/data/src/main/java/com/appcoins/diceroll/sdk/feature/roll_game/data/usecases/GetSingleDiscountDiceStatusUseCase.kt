package com.appcoins.diceroll.sdk.feature.roll_game.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.SingleDiscountDiceDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSingleDiscountDiceStatusUseCase @Inject constructor(private val datastore: SingleDiscountDiceDataSource) {

    operator fun invoke(): Flow<Boolean> = datastore.getSingleDiscountDiceStatus()
}
