package com.appcoins.diceroll.sdk.feature.roll_game.ui

/**
 * A sealed hierarchy describing the roll game state for the initial screen.
 * This is used to control the visibility of the content in the roll game.
 */

sealed interface RollGameState {

  /**
   * The screen is visible and loading its content.
   */
  data object Loading : RollGameState

  /**
   * The screen is visible, but there was an Error when loading the content.
   */
  data object Error : RollGameState

  /**
   * The screen is visible and the content was loaded successfully.
   */
  data class Success(
    val attemptsLeft: Int?
  ) : RollGameState
}
