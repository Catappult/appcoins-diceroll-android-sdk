package com.appcoins.diceroll.sdk.feature.roll_game.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.PrepaidDiceDataSource
import javax.inject.Inject

class UpdatePrepaidDiceStatusUseCase @Inject constructor(private val datastore: PrepaidDiceDataSource) {

    suspend operator fun invoke(active: Boolean) = datastore.savePrepaidDiceStatus(active)
}
