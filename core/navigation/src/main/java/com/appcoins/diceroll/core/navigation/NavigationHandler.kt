package com.appcoins.diceroll.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet


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

internal fun NavGraphBuilder.dialogHandler(
  route: String,
  content: @Composable (Map<String, String>) -> Unit,
  destinationArgs: List<NamedNavArgument>,
  destinationDeeplinks: List<NavDeepLink>,
) {
  dialog(
    route = route,
    arguments = destinationArgs,
    deepLinks = destinationDeeplinks,
  ) { backStackEntry ->
    val argValues =
      destinationArgs.associate { it.name to backStackEntry.arguments?.getString(it.name) }
    content(argValues.filterValues { it != null }.mapValues { it.value!! })
  }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
internal fun NavGraphBuilder.bottomSheetHandler(
  route: String,
  content: @Composable (Map<String, String>) -> Unit,
  destinationArgs: List<NamedNavArgument>,
  destinationDeeplinks: List<NavDeepLink>,
) {
  bottomSheet(
    route = route,
    arguments = destinationArgs,
    deepLinks = destinationDeeplinks,
  ) { backStackEntry ->
    val argValues =
      destinationArgs.associate { it.name to backStackEntry.arguments?.getString(it.name) }
    content(argValues.filterValues { it != null }.mapValues { it.value!! })
  }
}
