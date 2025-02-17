package com.appcoins.diceroll.sdk.core.navigation.destinations

sealed class Destinations(open val route: String) {
    sealed class Screen(override val route: String) : Destinations(route) {
        data object RollGame : Screen("roll_game_screen")
        data object Store : Screen("store_screen")
        data object Settings : Screen("settings_screen")
    }
}

fun Destinations.toNavigationType(): NavigationType {
    return when (this) {
        is Destinations.Screen -> NavigationType.Composable
    }
}

sealed interface NavigationType {
    data object Composable : NavigationType
}
