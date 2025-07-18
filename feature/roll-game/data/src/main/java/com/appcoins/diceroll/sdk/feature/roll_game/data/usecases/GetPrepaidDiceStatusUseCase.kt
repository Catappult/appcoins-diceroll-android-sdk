package com.appcoins.diceroll.sdk.feature.roll_game.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.PrepaidDiceDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPrepaidDiceStatusUseCase @Inject constructor(private val datastore: PrepaidDiceDataSource) {

    operator fun invoke(): Flow<Boolean> = datastore.getPrepaidDiceStatus()
}
