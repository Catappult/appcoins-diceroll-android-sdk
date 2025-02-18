package com.appcoins.diceroll.sdk.feature.settings.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.appcoins.diceroll.sdk.core.navigation.buildDestinationRoute
import com.appcoins.diceroll.sdk.core.navigation.destinations.Destinations
import com.appcoins.diceroll.sdk.core.navigation.navigateToDestination
import com.appcoins.diceroll.sdk.feature.settings.ui.SettingsRoute

fun NavController.navigateToSettingsScreen(navOptions: NavOptions) {
    this.navigateToDestination(
        destination = Destinations.Screen.Settings,
        navOptions = navOptions
    )
}

fun NavGraphBuilder.settingsRoute() {
    this.buildDestinationRoute(
        destination = Destinations.Screen.Settings,
    ) {
        SettingsRoute()
    }
}
