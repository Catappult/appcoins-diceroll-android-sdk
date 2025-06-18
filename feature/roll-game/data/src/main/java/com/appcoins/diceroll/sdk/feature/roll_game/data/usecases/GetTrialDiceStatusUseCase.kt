package com.appcoins.diceroll.sdk.feature.roll_game.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.TrialDiceDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrialDiceStatusUseCase @Inject constructor(private val datastore: TrialDiceDataSource) {

    operator fun invoke(): Flow<Boolean> = datastore.getTrialDiceStatus()
}
