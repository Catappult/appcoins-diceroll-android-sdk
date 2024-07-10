package com.appcoins.diceroll.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.appcoins.diceroll.core.ui.design.DiceRollIcons
import com.appcoins.diceroll.core.ui.widgets.components.DiceRollNavigationBarItem
import com.appcoins.diceroll.core.ui.widgets.components.DiceRollTopAppBar
import com.appcoins.diceroll.navigation.DiceRollNavHost
import com.appcoins.diceroll.navigation.TopLevelDestination
import com.appcoins.diceroll.core.utils.R
import com.appcoins.diceroll.feature.settings.ui.navigation.navigateToSettings
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun DiceRollApp() {
  val appState: DiceRollAppState = rememberDiceRollAppState()
  Scaffold(
    topBar = {
      DiceRollTopAppBar(
        titleRes = appState.currentTopLevelDestination?.titleTextId ?: R.string.top_bar_title,
        actionIcon = DiceRollIcons.settings,
        onActionClick = { appState.navController.navigateToSettings() },
      )
    },
    bottomBar = {
      DiceRollBottomBar(
        destinations = appState.topLevelDestinations,
        onNavigateToDestination = appState::navigateToTopLevelDestination,
        currentDestination = appState.currentDestination,
      )
    },
  ) { scaffoldPadding ->
    DiceRollNavHost(
      appState.navController,
      appState.bottomSheetNavigator,
      scaffoldPadding = scaffoldPadding,
    )
  }
}

@Composable
fun DiceRollBottomBar(
  destinations: List<TopLevelDestination>,
  onNavigateToDestination: (TopLevelDestination) -> Unit,
  currentDestination: NavDestination?,
) {
  NavigationBar(
    modifier = Modifier,
    containerColor = MaterialTheme.colorScheme.background,
  ) {
    destinations.forEach { destination ->
      DiceRollNavigationBarItem(
        selected = currentDestination.isTopLevelDestinationInHierarchy(destination),
        onClick = { onNavigateToDestination(destination) },
        icon = {
          Icon(
            imageVector = destination.icon,
            contentDescription = null,
          )
        },
        label = { Text(stringResource(destination.iconTextId)) },
      )
    }
  }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
  this?.hierarchy?.any {
    it.route?.contains(destination.name, true) ?: false
  } ?: false
