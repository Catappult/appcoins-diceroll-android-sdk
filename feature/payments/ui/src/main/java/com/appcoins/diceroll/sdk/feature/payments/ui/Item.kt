package com.appcoins.diceroll.sdk.feature.payments.ui

/**
 * Payment item in game to represent and match a given SKU.
 */
sealed class Item  {
  data class Attempts(val currentAttempts: Int) : Item()
}

/**
 * Map a [Item] to a SKU string from Catappult if possible.
 */
fun Item.toSku(): String = when (this) {
  is Item.Attempts -> "attempts"
}
