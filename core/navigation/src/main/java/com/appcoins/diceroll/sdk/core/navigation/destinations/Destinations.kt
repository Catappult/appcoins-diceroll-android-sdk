package com.appcoins.diceroll.sdk.core.navigation.destinations

sealed class Destinations(open val route: String) {
  sealed class Screen(override val route: String) : Destinations(route) {
    data object RollGame : Screen("roll_game_screen")
    data object Stats : Screen("stats_screen")
    data object StatsRollDetails : Screen("stats_roll_details_screen")
  }

  sealed class BottomSheet(override val route: String) : Destinations(route) {
    data object PaymentProcess : BottomSheet("payment_process_bottom_sheet")
  }

  sealed class Dialog(override val route: String) : Destinations(route) {
    data object Settings : Dialog("settings_dialog")
  }
}

fun Destinations.toNavigationType(): NavigationType {
  return when (this) {
    is Destinations.Screen -> NavigationType.Composable
    is Destinations.BottomSheet -> NavigationType.BottomSheet
    is Destinations.Dialog -> NavigationType.Dialog
  }
}

sealed interface NavigationType {
  data object Composable : NavigationType
  data object Dialog : NavigationType
  data object BottomSheet : NavigationType
}