package com.appcoins.diceroll.core.ui.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.google.accompanist.systemuicontroller.rememberSystemUiController

val lightAndroidBackgroundTheme = BackgroundTheme(color = light_grey)
val darkAndroidBackgroundTheme = BackgroundTheme(color = dark_blue)

/**
 * App theme
 * @param darkTheme Whether the theme should use a dark color scheme (is dark by default).
 */
@Composable
fun DiceRollTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit,
) {
  UpdateSystemBarsTheme(darkTheme = darkTheme)
  val colorScheme = if (darkTheme) darkAppColorScheme else lightAppColorScheme
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
    systemUiController.setSystemBarsColor(color = dark_blue)
  } else {
    systemUiController.setSystemBarsColor(color = light_grey)
  }
}