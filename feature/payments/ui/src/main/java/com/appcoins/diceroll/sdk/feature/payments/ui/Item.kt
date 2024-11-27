package com.appcoins.diceroll.sdk.feature.payments.ui

/**
 * Payment item in game to represent and match a given SKU.
 */
sealed class Item(open val sku: String) {
    data class Attempts(val currentAttempts: Int) : Item(ATTEMPTS_SKU)
    data object GoldDice : Item(GOLD_DICE_SKU)

    companion object {
        const val ATTEMPTS_SKU = "attempts"
        const val GOLD_DICE_SKU = "golden_dice"
    }
}
