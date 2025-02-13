package com.appcoins.diceroll.sdk.feature.store.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.appcoins.diceroll.sdk.core.navigation.buildDestinationRoute
import com.appcoins.diceroll.sdk.core.navigation.destinations.Destinations
import com.appcoins.diceroll.sdk.core.navigation.navigateToDestination
import com.appcoins.diceroll.sdk.feature.store.ui.StoreRoute

fun NavController.navigateToStoreScreen(navOptions: NavOptions) {
    this.navigateToDestination(
        destination = Destinations.Screen.Store,
        navOptions = navOptions
    )
}

fun NavGraphBuilder.storeRoute() {
    this.buildDestinationRoute(
        destination = Destinations.Screen.Store,
    ) {
        StoreRoute()
    }
}
