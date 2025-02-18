package com.appcoins.diceroll.sdk.core.ui.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
    goldenDiceTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    UpdateSystemBarsTheme(darkTheme = darkTheme)
    val colorScheme = if (darkTheme) {
        if (goldenDiceTheme) {
            darkGoldenDiceAppColorScheme
        } else {
            darkAppColorScheme
        }
    } else {
        if (goldenDiceTheme) {
            darkGoldenDiceAppColorScheme
        } else {
            darkAppColorScheme
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
