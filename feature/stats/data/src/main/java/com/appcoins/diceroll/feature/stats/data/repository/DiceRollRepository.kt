package com.appcoins.diceroll.feature.stats.data.repository

import com.appcoins.diceroll.core.db.model.DiceRollDao
import com.appcoins.diceroll.feature.stats.data.model.DiceRoll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DiceRollRepository @Inject constructor(
  private val dao: DiceRollDao,
) {
  fun getBdDiceRolls(): Flow<List<DiceRoll>> =
    dao.getDiceRollEntityListFlow().map { entityList ->
      entityList.map { rollEntity ->
        rollEntity.mapToDiceRoll()
      }
    }.flowOn(Dispatchers.IO)

  suspend fun saveDiceRoll(diceRoll: DiceRoll) {
    dao.saveDiceRollEntity(diceRoll.mapToDiceRollEntity())
  }

  fun getAttemptsLeft() : Flow<Int?> {
    return dao.getAttemptsLeft()
  }
}