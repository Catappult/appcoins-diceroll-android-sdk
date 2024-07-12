package com.appcoins.diceroll.sdk.core.navigation.destinations

object DestinationArgs {

  /**
   * Argument to be passed to [Destinations.BottomSheet.PaymentProcess] representing the item sku to
   * be used in the payment.
   */
  const val ITEM_ID = "item_id"

  /**
   * Argument to be passed to [Destinations.BottomSheet.PaymentProcess] representing the number of
   * attempt left when the payment bottom sheet was called.
   */
  const val ATTEMPTS_LEFT = "attempts_left"
}