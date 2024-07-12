package com.appcoins.diceroll.sdk.feature.settings.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.appcoins.diceroll.sdk.core.navigation.buildDestinationRoute
import com.appcoins.diceroll.sdk.core.navigation.destinations.Destinations
import com.appcoins.diceroll.sdk.core.navigation.navigateToDestination
import com.appcoins.diceroll.sdk.feature.settings.ui.SettingsRoute

fun NavController.navigateToSettings() {
  this.navigateToDestination(
    destination = Destinations.Dialog.Settings
  )
}

fun NavGraphBuilder.settingsRoute(onDismiss: () -> Unit) {
  this.buildDestinationRoute(
    destination = Destinations.Dialog.Settings,
  ) {
    SettingsRoute(onDismiss)
  }
}
