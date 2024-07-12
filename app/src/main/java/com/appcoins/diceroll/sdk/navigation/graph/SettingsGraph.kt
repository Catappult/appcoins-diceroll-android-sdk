package com.appcoins.diceroll.sdk.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.appcoins.diceroll.sdk.feature.settings.ui.navigation.settingsRoute

internal fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
  settingsRoute { navController.navigateUp() }
}