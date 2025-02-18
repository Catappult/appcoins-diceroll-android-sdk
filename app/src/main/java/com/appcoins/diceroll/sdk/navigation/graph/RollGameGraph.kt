package com.appcoins.diceroll.sdk.navigation.graph

import androidx.navigation.NavGraphBuilder
import com.appcoins.diceroll.sdk.feature.roll_game.ui.navigation.rollGameRoute

internal fun NavGraphBuilder.rollGameGraph() {
    rollGameRoute()
}
