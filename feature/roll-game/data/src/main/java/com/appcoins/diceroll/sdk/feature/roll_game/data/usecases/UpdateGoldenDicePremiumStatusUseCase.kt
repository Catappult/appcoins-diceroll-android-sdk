package com.appcoins.diceroll.sdk.feature.roll_game.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.GoldenDicePremiumDataSource
import javax.inject.Inject

class UpdateGoldenDicePremiumStatusUseCase @Inject constructor(private val datastore: GoldenDicePremiumDataSource) {

    suspend operator fun invoke(active: Boolean) = datastore.saveGoldenDicePremiumStatus(active)
}
