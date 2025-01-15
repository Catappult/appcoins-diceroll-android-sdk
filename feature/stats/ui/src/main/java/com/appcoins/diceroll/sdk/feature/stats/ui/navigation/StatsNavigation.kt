package com.appcoins.diceroll.sdk.feature.stats.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.appcoins.diceroll.sdk.core.navigation.destinations.Destinations
import com.appcoins.diceroll.sdk.core.navigation.buildDestinationRoute
import com.appcoins.diceroll.sdk.core.navigation.navigateToDestination
import com.appcoins.diceroll.sdk.feature.stats.data.model.DiceRoll
import com.appcoins.diceroll.sdk.feature.stats.ui.StatsRoute

fun NavController.navigateToStoreScreen(navOptions: NavOptions) {
  this.navigateToDestination(
    destination = Destinations.Screen.Store,
    navOptions = navOptions
  )
}

fun NavGraphBuilder.storeRoute(onDetailsClick: (List<DiceRoll>) -> Unit) {
  this.buildDestinationRoute(
    destination = Destinations.Screen.Store,
  ) {
    StatsRoute(onDetailsClick)
  }
}
