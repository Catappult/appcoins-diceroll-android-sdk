package com.appcoins.diceroll.sdk.feature.roll_game.data.usecases

import com.appcoins.diceroll.sdk.feature.roll_game.data.AttemptsDataSource
import com.appcoins.diceroll.sdk.feature.roll_game.data.DEFAULT_ATTEMPTS_NUMBER
import javax.inject.Inject

class AddAttemptsUseCase @Inject constructor(private val datastore: AttemptsDataSource) {

    suspend operator fun invoke() = datastore.addAttempts(DEFAULT_ATTEMPTS_NUMBER)
}
