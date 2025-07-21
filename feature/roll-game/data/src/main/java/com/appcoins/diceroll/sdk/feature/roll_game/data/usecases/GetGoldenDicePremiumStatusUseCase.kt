package com.appcoins.diceroll.sdk.feature.roll_game.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.GoldenDicePremiumDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoldenDicePremiumStatusUseCase @Inject constructor(private val datastore: GoldenDicePremiumDataSource) {

    operator fun invoke(): Flow<Boolean> = datastore.getGoldenDicePremiumStatus()
}
