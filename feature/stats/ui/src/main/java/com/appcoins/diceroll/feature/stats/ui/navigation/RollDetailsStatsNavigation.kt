package com.appcoins.diceroll.feature.stats.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.appcoins.diceroll.core.navigation.destinations.Destinations
import com.appcoins.diceroll.core.navigation.buildDestinationRoute
import com.appcoins.diceroll.core.navigation.navigateToDestination
import com.appcoins.diceroll.feature.stats.ui.RollDetailsStatsRoute

fun NavController.navigateToRollDetailsStatsScreen() {
  this.navigateToDestination(
    destination = Destinations.Screen.StatsRollDetails,
  )
}

fun NavGraphBuilder.rollDetailsStatsRoute() {
  this.buildDestinationRoute(
    destination = Destinations.Screen.StatsRollDetails,
  ) {
    RollDetailsStatsRoute()
  }
}
