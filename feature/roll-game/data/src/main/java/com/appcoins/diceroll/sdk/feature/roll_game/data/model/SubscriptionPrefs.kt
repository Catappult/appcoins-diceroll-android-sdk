package com.appcoins.diceroll.sdk.feature.roll_game.data.model

data class SubscriptionPrefs(
    val availableSubscriptions: List<Subscription> = emptyList(),
    val selectedSubscription: Subscription = Subscription.DEFAULT
)

enum class Subscription {
    DEFAULT,
    GOLDEN_DICE,
    GOLDEN_DICE_PREMIUM,
    TRIAL_DICE,
    PREPAID_DICE,
    SINGLE_DISCOUNT_DICE,
    RECURRING_DISCOUNT_DICE,
}
