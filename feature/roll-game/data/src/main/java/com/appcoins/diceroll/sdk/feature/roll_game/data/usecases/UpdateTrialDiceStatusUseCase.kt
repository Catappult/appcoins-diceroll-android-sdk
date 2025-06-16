package com.appcoins.diceroll.sdk.feature.roll_game.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.TrialDiceDataSource
import javax.inject.Inject

class UpdateTrialDiceStatusUseCase @Inject constructor(private val datastore: TrialDiceDataSource) {

    suspend operator fun invoke(active: Boolean) = datastore.saveTrialDiceStatus(active)
}
