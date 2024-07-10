package com.appcoins.diceroll.core.ui.widgets.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * @param selected Whether this item is selected.
 * @param onClick The callback to be invoked when this item is selected.
 * @param icon The item icon content.
 * @param modifier Modifier to be applied to this item.
 * @param selectedIcon The item icon content when selected.
 * @param label The item text label content.
 */
@Composable
fun RowScope.DiceRollNavigationBarItem(
  selected: Boolean,
  onClick: () -> Unit,
  icon: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  selectedIcon: @Composable () -> Unit = icon,
  label: @Composable (() -> Unit)? = null,
) {
  NavigationBarItem(
    selected = selected,
    onClick = onClick,
    icon = if (selected) selectedIcon else icon,
    modifier = modifier,
    label = label,
  )
}