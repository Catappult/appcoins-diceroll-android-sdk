package com.appcoins.diceroll.core.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DiceRollEntity")
data class DiceRollEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Int?,
  val rollWin: Boolean,
  val guessNumber: Int,
  val resultNumber: Int,
  val attemptsLeft: Int
)