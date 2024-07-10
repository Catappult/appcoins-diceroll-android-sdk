package com.appcoins.diceroll.core.ui.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * A class to model background color
 */
@Immutable
data class BackgroundTheme(
  val color: Color = Color.Unspecified
)

/**
 * A composition local for [BackgroundTheme].
 */
val LocalBackgroundTheme = staticCompositionLocalOf { BackgroundTheme() }
