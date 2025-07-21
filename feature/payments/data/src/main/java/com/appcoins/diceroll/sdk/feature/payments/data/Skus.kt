package com.appcoins.diceroll.sdk.feature.payments.data

object Skus {
    const val ATTEMPTS = "attempts"
    const val NON_CONSUMABLE_ATTEMPTS = "non_consumable_attempts"
    const val NON_EXISTENT = "non_existent"

    // Normal Subscription
    const val GOLDEN_DICE = "golden_dice"
    // Upgrade Premium Subscription
    const val GOLDEN_DICE_PREMIUM = "golden_dice_premium"
    // Free Trial Subscription
    const val TRIAL_DICE = "trial_dice"
    // Prepaid Subscription
    const val PREPAID_DICE = "prepaid_dice"
    // Single Discount Subscription
    const val SINGLE_DISCOUNT_DICE = "single_discount_dice"
    // Recurring Discount Subscription
    const val RECURRING_DISCOUNT_DICE = "recurring_discount_dice"

    val INAPPS =
        listOf(
            ATTEMPTS,
            NON_CONSUMABLE_ATTEMPTS,
            NON_EXISTENT,
        )
    val SUBS =
        listOf(
            GOLDEN_DICE,
            GOLDEN_DICE_PREMIUM,
            TRIAL_DICE,
            PREPAID_DICE,
            SINGLE_DISCOUNT_DICE,
            RECURRING_DISCOUNT_DICE,
        )
}
