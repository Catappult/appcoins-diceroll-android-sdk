package com.appcoins.diceroll.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.appcoins.diceroll.core.navigation.destinations.Destinations
import com.appcoins.diceroll.navigation.graph.rollGameGraph
import com.appcoins.diceroll.navigation.graph.settingsGraph
import com.appcoins.diceroll.navigation.graph.statsGraph
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun DiceRollNavHost(
  navController: NavHostController,
  bottomSheetNavigator: BottomSheetNavigator,
  startDestination: Destinations = Destinations.Screen.RollGame,
  scaffoldPadding: PaddingValues
) {
  ModalBottomSheetLayout(bottomSheetNavigator = bottomSheetNavigator) {
    NavHost(
      navController = navController,
      startDestination = startDestination.route,
      modifier = Modifier.padding(scaffoldPadding),
    ) {
      settingsGraph(navController)
      statsGraph(navController)
      rollGameGraph(navController)
    }
  }
}
