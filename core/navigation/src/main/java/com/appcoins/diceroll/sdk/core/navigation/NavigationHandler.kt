package com.appcoins.diceroll.sdk.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

internal fun finalRoute(
  destination: String,
  destinationArgs: List<String> = emptyList(),
  shouldNavigate: Boolean = false,
): String {
  val routeArgs = destinationArgs.joinToString("/") {
    if (shouldNavigate) it else "{$it}"
  }

  return if (routeArgs.isNotEmpty()) {
    "$destination/$routeArgs"
  } else {
    destination
  }
}

internal fun NavGraphBuilder.composableHandler(
  route: String,
  content: @Composable (Map<String, String>) -> Unit,
  destinationArgs: List<NamedNavArgument>,
  destinationDeeplinks: List<NavDeepLink>,
  shouldNavigate: Boolean
) {
  composable(
    route = route,
    arguments = destinationArgs,
    deepLinks = destinationDeeplinks,
    enterTransition = {
      if (shouldNavigate) {
        slideIntoContainer(
          towards = AnimatedContentTransitionScope.SlideDirection.Left,
          animationSpec = tween(300)
        )
      } else {
        null
      }
    },
    exitTransition = {
      if (shouldNavigate) {
        slideOutOfContainer(
          towards = AnimatedContentTransitionScope.SlideDirection.Left,
          animationSpec = tween(300)
        )
      } else {
        null
      }
    },
    popEnterTransition = {
      if (shouldNavigate) {
        slideIntoContainer(
          towards = AnimatedContentTransitionScope.SlideDirection.Right,
          animationSpec = tween(300)
        )
      } else {
        null
      }
    },
    popExitTransition = {
      if (shouldNavigate) {
        slideOutOfContainer(
          towards = AnimatedContentTransitionScope.SlideDirection.Right,
          animationSpec = tween(300)
        )
      } else {
        null
      }
    },
  ) { backStackEntry ->
    val argValues =
      destinationArgs.associate { it.name to backStackEntry.arguments?.getString(it.name) }
    content(argValues.filterValues { it != null }.mapValues { it.value!! })
  }
}
