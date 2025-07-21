package com.appcoins.diceroll.sdk.core.ui.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.DEFAULT
import com.google.accompanist.systemuicontroller.rememberSystemUiController

val lightAndroidBackgroundTheme = BackgroundTheme(color = blue_background)
val darkAndroidBackgroundTheme = BackgroundTheme(color = blue_background)

/**
 * App theme
 * @param darkTheme Whether the theme should use a dark color scheme (is dark by default).
 */
@Composable
fun DiceRollTheme(
    darkTheme: Boolean = true,
    subscriptionTypeDiceTheme: Subscription = DEFAULT,
    content: @Composable () -> Unit,
) {
    UpdateSystemBarsTheme(darkTheme = darkTheme)
    val colorScheme = if (darkTheme) {
        when (subscriptionTypeDiceTheme) {
            Subscription.GOLDEN_DICE, Subscription.GOLDEN_DICE_PREMIUM -> darkGoldenDiceAppColorScheme
            Subscription.TRIAL_DICE -> darkTrialDiceAppColorScheme
            Subscription.PREPAID_DICE -> darkPrepaidDiceAppColorScheme
            Subscription.SINGLE_DISCOUNT_DICE -> darkSingleDiscountDiceAppColorScheme
            Subscription.RECURRING_DISCOUNT_DICE -> darkRecurringDiscountDiceAppColorScheme
            else -> darkAppColorScheme
        }
    } else {
        when (subscriptionTypeDiceTheme) {
            Subscription.GOLDEN_DICE, Subscription.GOLDEN_DICE_PREMIUM -> darkGoldenDiceAppColorScheme
            Subscription.TRIAL_DICE -> darkTrialDiceAppColorScheme
            Subscription.PREPAID_DICE -> darkPrepaidDiceAppColorScheme
            Subscription.SINGLE_DISCOUNT_DICE -> darkSingleDiscountDiceAppColorScheme
            Subscription.RECURRING_DISCOUNT_DICE -> darkRecurringDiscountDiceAppColorScheme
            else -> darkAppColorScheme
        }
    }
    val backgroundTheme = if (darkTheme) darkAndroidBackgroundTheme else lightAndroidBackgroundTheme

    CompositionLocalProvider(
        LocalBackgroundTheme provides backgroundTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = shapes,
            typography = DiceRollTypography,
            content = content,
        )
    }
}

@Composable
private fun UpdateSystemBarsTheme(darkTheme: Boolean) {
    val systemUiController = rememberSystemUiController()
    if (darkTheme) {
        systemUiController.setSystemBarsColor(color = blue_background)
    } else {
        systemUiController.setSystemBarsColor(color = blue_background)
    }
}
