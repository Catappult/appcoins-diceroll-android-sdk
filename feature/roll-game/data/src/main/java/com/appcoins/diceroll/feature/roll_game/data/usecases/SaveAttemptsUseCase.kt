package com.appcoins.diceroll.feature.roll_game.data.usecases

import com.appcoins.diceroll.feature.roll_game.data.AttemptsDataSource
import javax.inject.Inject

class SaveAttemptsUseCase @Inject constructor(private val datastore: AttemptsDataSource) {

  suspend operator fun invoke(attemptsLeft: Int) = datastore.saveAttemptsLeft(attemptsLeft)
}