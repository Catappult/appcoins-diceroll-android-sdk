package com.appcoins.diceroll.sdk.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.appcoins.diceroll.sdk.core.navigation.destinations.Destinations
import com.appcoins.diceroll.sdk.core.navigation.destinations.NavigationType
import com.appcoins.diceroll.sdk.core.navigation.destinations.toNavigationType

/**
 * Navigates to the specified destination using the given route and navigation options.
 *
 * @param destination The destination to navigate to, providing the type of screen.
 * @param destinationArgs The list of optional destination arguments.
 * @param navOptions The navigation options for this navigation.
 */
fun NavController.navigateToDestination(
  destination: Destinations,
  destinationArgs: List<String> = emptyList(),
  navOptions: NavOptions? = null,
) {
  this.navigate(finalRoute(destination.route, destinationArgs, shouldNavigate = true), navOptions)
}

/**
 * Builds a screen for the specified destination, taking into consideration the type of screen that
 * should be created a [Destinations.Screen] by passing the destination object, with optional arguments and
 * deeplink, and with the provided Composable content.
 *
 * @param destination The destination to build the screen for, providing the type of screen.
 * @param destinationArgs The list of optional destination arguments.
 * @param destinationDeeplinks The list of optional destination DeepLinks.
 * @param shouldAnimate Adds a slide animation when navigating to the destination, but its only
 * applicable to the type [Destinations.Screen] and will be ignored for the other types.
 * @param destinationComposable The Composable content to be displayed on the screen.
 */
fun NavGraphBuilder.buildDestinationRoute(
  destination: Destinations,
  destinationArgs: List<String> = emptyList(),
  destinationDeeplinks: List<String> = emptyList(),
  shouldAnimate: Boolean = false,
  destinationComposable: @Composable (Map<String, String>) -> Unit,
) {
  val finalRoute = finalRoute(destination.route, destinationArgs, shouldNavigate = false)

  val destinationArgsList = destinationArgs.map {
    navArgument(it) {
      type = NavType.StringType
    }
  }

  val destinationDeeplinksList = destinationDeeplinks.map {
    navDeepLink {
      uriPattern = it
    }
  }

  when (destination.toNavigationType()) {
    NavigationType.Composable -> {
      composableHandler(
        route = finalRoute,
        destinationArgs = destinationArgsList,
        destinationDeeplinks = destinationDeeplinksList,
        content = destinationComposable,
        shouldNavigate = shouldAnimate,
      )
    }
  }
}
