package com.appcoins.diceroll.sdk.feature.payments.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.appcoins.diceroll.sdk.core.navigation.buildDestinationRoute
import com.appcoins.diceroll.sdk.core.navigation.destinations.DestinationArgs
import com.appcoins.diceroll.sdk.core.navigation.destinations.Destinations
import com.appcoins.diceroll.sdk.core.navigation.navigateToDestination
import com.appcoins.diceroll.sdk.core.utils.extensions.ifLet
import com.appcoins.diceroll.sdk.feature.payments.ui.AttemptsPaymentProcessBottomSheetRoute
import com.appcoins.diceroll.sdk.feature.payments.ui.GoldDicePaymentProcessBottomSheetRoute
import com.appcoins.diceroll.sdk.feature.payments.ui.Item

fun NavController.navigateToPaymentResultBottomSheet(item: Item) {
    val listOfArguments = arrayListOf(item.sku)

    if (item is Item.Attempts) {
        listOfArguments.add(item.currentAttempts.toString())
    }

    this.navigateToDestination(
        destination = Destinations.BottomSheet.PaymentProcess,
        destinationArgs = listOfArguments,
        navOptions = navOptions {
            launchSingleTop = true
        }
    )
}

/**
 * Payment dialog route, that receives a [itemId] that is the SKU for the item to buy.
 * This item could be the Item instead of the string but its not recommended to use complex data
 * when navigating, according to the official android documentation.
 * Doing so creates problems, such as the route not being able to be parsed by the navigation
 * since it would need either a Parcelable or a Serializable and that issue wont be fixed as seen
 * in the issue tracker below.
 *
 * @see <a href="https://issuetracker.google.com/issues/148523779">Issue Tracker</a>
 *
 */
fun NavGraphBuilder.paymentProcessRoute(onDismiss: () -> Unit) {
    this.buildDestinationRoute(
        destination = Destinations.BottomSheet.PaymentProcess,
        destinationArgs = listOf(DestinationArgs.ITEM_ID, DestinationArgs.ATTEMPTS_LEFT),
    ) { args ->
        ifLet(
            args[DestinationArgs.ITEM_ID],
            args[DestinationArgs.ATTEMPTS_LEFT]
        ) { (itemId, attempts) ->
            AttemptsPaymentProcessBottomSheetRoute(onDismiss, itemId, attempts)
        }
    }

    this.buildDestinationRoute(
        destination = Destinations.BottomSheet.PaymentProcess,
        destinationArgs = listOf(DestinationArgs.ITEM_ID),
    ) { args ->
        ifLet(args[DestinationArgs.ITEM_ID]) { (itemId) ->
            GoldDicePaymentProcessBottomSheetRoute(onDismiss, itemId)
        }
    }
}
