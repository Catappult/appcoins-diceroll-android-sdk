package com.appcoins.diceroll.sdk.navigation.graph

import androidx.navigation.NavGraphBuilder
import com.appcoins.diceroll.sdk.feature.store.ui.navigation.storeRoute

internal fun NavGraphBuilder.storeGraph() {
    storeRoute()
}
