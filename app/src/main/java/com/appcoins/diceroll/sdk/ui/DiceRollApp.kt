package com.appcoins.diceroll.sdk.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.appcoins.diceroll.sdk.core.ui.widgets.components.DiceRollNavigationBarItem
import com.appcoins.diceroll.sdk.navigation.DiceRollNavHost
import com.appcoins.diceroll.sdk.navigation.TopLevelDestination
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun DiceRollApp() {
    val appState: DiceRollAppState = rememberDiceRollAppState()
    Scaffold(
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
