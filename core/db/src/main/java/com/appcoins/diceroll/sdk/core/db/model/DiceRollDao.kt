package com.appcoins.diceroll.sdk.core.db.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DiceRollDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveDiceRollEntity(diceRollEntity: DiceRollEntity)

  @Transaction
  suspend fun clearAndSaveDiceRolls(diceRollList: List<DiceRollEntity>) {
    clearDiceRollEntity()
    diceRollList.forEach { saveDiceRollEntity(it) }
  }

  @Transaction
  suspend fun saveDiceRolls(diceRollList: List<DiceRollEntity>) {
    diceRollList.forEach { saveDiceRollEntity(it) }
  }

  @Query("SELECT * FROM DiceRollEntity")
  fun getDiceRollEntityListFlow(): Flow<List<DiceRollEntity>>

  @Query("DELETE FROM DiceRollEntity")
  fun clearDiceRollEntity()

  @Query("SELECT * FROM DiceRollEntity WHERE id = :roll")
  suspend fun getDiceRoll(roll: String): DiceRollEntity?

  @Query("SELECT attemptsLeft FROM DiceRollEntity ORDER BY id DESC LIMIT 1 ")
  fun getAttemptsLeft(): Flow<Int?>
}
