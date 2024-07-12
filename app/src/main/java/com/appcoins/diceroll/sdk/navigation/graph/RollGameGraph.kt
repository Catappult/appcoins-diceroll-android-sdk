package com.appcoins.diceroll.sdk.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.appcoins.diceroll.sdk.feature.payments.ui.Item
import com.appcoins.diceroll.sdk.feature.payments.ui.navigation.navigateToPaymentResultBottomSheet
import com.appcoins.diceroll.sdk.feature.payments.ui.navigation.paymentProcessRoute
import com.appcoins.diceroll.sdk.feature.payments.ui.toSku
import com.appcoins.diceroll.sdk.feature.roll_game.ui.navigation.rollGameRoute

internal fun NavGraphBuilder.rollGameGraph(navController: NavHostController) {
  rollGameRoute(onBuyClick = { item ->
    navController.navigateToPaymentResultBottomSheet(
      item.toSku(),
      (item as Item.Attempts).currentAttempts.toString()
    )
  })
  paymentProcessRoute(onDismiss = {
    navController.navigateUp()
  })
}