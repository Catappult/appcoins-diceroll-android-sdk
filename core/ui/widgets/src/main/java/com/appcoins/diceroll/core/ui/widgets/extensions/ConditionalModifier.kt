package com.appcoins.diceroll.core.ui.widgets.extensions

import androidx.compose.ui.Modifier

/**
 * Apply a modifier if a condition is true
 */
fun Modifier.conditional(
  condition: Boolean,
  ifTrue: Modifier.() -> Modifier,
  ifFalse: (Modifier.() -> Modifier)? = null
): Modifier {
  return if (condition) {
    then(ifTrue(Modifier))
  } else if (ifFalse != null) {
    then(ifFalse(Modifier))
  } else {
    this
  }
}