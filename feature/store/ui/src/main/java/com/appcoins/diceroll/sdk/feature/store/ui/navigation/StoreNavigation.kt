package com.appcoins.diceroll.sdk.feature.store.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.appcoins.diceroll.sdk.core.navigation.buildDestinationRoute
import com.appcoins.diceroll.sdk.core.navigation.destinations.Destinations
import com.appcoins.diceroll.sdk.core.navigation.navigateToDestination
import com.appcoins.diceroll.sdk.feature.payments.ui.Item
import com.appcoins.diceroll.sdk.feature.store.ui.StoreRoute

fun NavController.navigateToStoreScreen(navOptions: NavOptions) {
    this.navigateToDestination(
        destination = Destinations.Screen.Store,
        navOptions = navOptions
    )
}

fun NavGraphBuilder.storeRoute(onBuyClick: (Item) -> Unit) {
    this.buildDestinationRoute(
        destination = Destinations.Screen.Store,
    ) {
        StoreRoute(onBuyClick)
    }
}
