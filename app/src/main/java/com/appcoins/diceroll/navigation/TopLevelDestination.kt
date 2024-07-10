package com.appcoins.diceroll.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.appcoins.diceroll.core.ui.design.DiceRollIcons
import com.appcoins.diceroll.core.utils.R

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
  val icon: ImageVector,
  val iconTextId: Int,
  val titleTextId: Int,
) {
  GAME(
    icon = DiceRollIcons.game,
    iconTextId = R.string.roll_game_title,
    titleTextId = R.string.top_bar_title,
  ),
  STATS(
    icon = DiceRollIcons.stats,
    iconTextId = R.string.stats_title,
    titleTextId = R.string.top_bar_title,
  ),
}
