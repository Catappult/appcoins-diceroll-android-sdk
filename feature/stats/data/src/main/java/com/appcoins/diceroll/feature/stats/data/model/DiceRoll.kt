package com.appcoins.diceroll.feature.stats.data.model

data class DiceRoll(
  val id: Int?,
  val rollWin: Boolean,
  val guessNumber: Int,
  val resultNumber: Int,
  val attemptsLeft: Int,
)