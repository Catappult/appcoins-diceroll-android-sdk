package com.appcoins.diceroll.sdk.feature.roll_game.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.appcoins.diceroll.sdk.core.navigation.destinations.Destinations
import com.appcoins.diceroll.sdk.core.navigation.buildDestinationRoute
import com.appcoins.diceroll.sdk.core.navigation.navigateToDestination
import com.appcoins.diceroll.sdk.feature.roll_game.ui.RollGameRoute

fun NavController.navigateToRollGame(navOptions: NavOptions) {
  this.navigateToDestination(
    destination = Destinations.Screen.RollGame,
    navOptions = navOptions
  )
}

fun NavGraphBuilder.rollGameRoute() {
  this.buildDestinationRoute(
    destination = Destinations.Screen.RollGame,
  ) {
    RollGameRoute()
  }
}
