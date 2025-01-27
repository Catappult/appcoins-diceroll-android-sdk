package com.appcoins.diceroll.sdk.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.appcoins.diceroll.sdk.feature.payments.ui.navigation.navigateToPaymentResultBottomSheet
import com.appcoins.diceroll.sdk.feature.store.ui.navigation.storeRoute

internal fun NavGraphBuilder.storeGraph(navController: NavHostController) {
    storeRoute(onBuyClick = { item ->
        navController.navigateToPaymentResultBottomSheet(item)
    })
}