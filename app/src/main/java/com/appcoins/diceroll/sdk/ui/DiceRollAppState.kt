package com.appcoins.diceroll.sdk.ui

import androidx.compose.runtime.*
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.appcoins.diceroll.sdk.core.navigation.destinations.Destinations
import com.appcoins.diceroll.sdk.feature.roll_game.ui.navigation.navigateToRollGame
import com.appcoins.diceroll.sdk.feature.settings.ui.navigation.navigateToSettingsScreen
import com.appcoins.diceroll.sdk.feature.stats.ui.navigation.navigateToStoreScreen
import com.appcoins.diceroll.sdk.navigation.TopLevelDestination
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun rememberDiceRollAppState(
  bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
  navController: NavHostController = rememberNavController(bottomSheetNavigator)
): DiceRollAppState {
  return remember(navController) {
    DiceRollAppState(
      navController,
      bottomSheetNavigator,
    )
  }
}

@Stable
class DiceRollAppState @OptIn(ExperimentalMaterialNavigationApi::class) constructor(
  val navController: NavHostController,
  val bottomSheetNavigator: BottomSheetNavigator,
) {
  val currentDestination: NavDestination?
    @Composable get() = navController
      .currentBackStackEntryAsState().value?.destination

  val currentTopLevelDestination: TopLevelDestination?
    @Composable get() = when (currentDestination?.route) {
      Destinations.Screen.RollGame.route -> TopLevelDestination.GAME
      Destinations.Screen.Store.route -> TopLevelDestination.STORE
      Destinations.Screen.Settings.route -> TopLevelDestination.SETTINGS
      else -> null
    }

  val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

  /**
   * UI logic for navigating to a top level destination in the app. Top level destinations have
   * only one copy of the destination of the back stack, and save and restore state whenever you
   * navigate to and from it.
   *
   * @param topLevelDestination: The destination the app needs to navigate to.
   */
  fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
    val topLevelNavOptions = navOptions {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      // on the back stack as users select items
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
      }
      // Avoid multiple copies of the same destination when
      // reselecting the same item
      launchSingleTop = true
      // Restore state when reselecting a previously selected item
      restoreState = true
    }
    when (topLevelDestination) {
      TopLevelDestination.GAME -> navController.navigateToRollGame(topLevelNavOptions)
      TopLevelDestination.STORE -> navController.navigateToStoreScreen(topLevelNavOptions)
      TopLevelDestination.SETTINGS -> navController.navigateToSettingsScreen(topLevelNavOptions)
    }
  }
}
