package com.appcoins.diceroll.feature.roll_game.data.usecases

import com.appcoins.diceroll.feature.roll_game.data.AttemptsDataSource
import com.appcoins.diceroll.feature.roll_game.data.DEFAULT_ATTEMPTS_NUMBER
import javax.inject.Inject

class ResetAttemptsUseCase @Inject constructor(private val datastore: AttemptsDataSource) {

  suspend operator fun invoke() = datastore.saveAttemptsLeft(DEFAULT_ATTEMPTS_NUMBER)
}