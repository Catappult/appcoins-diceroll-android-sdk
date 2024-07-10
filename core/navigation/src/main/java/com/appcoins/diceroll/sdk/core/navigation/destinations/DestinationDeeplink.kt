package com.appcoins.diceroll.sdk.core.navigation.destinations

import androidx.core.net.toUri
import com.appcoins.diceroll.sdk.core.utils.diceRollPackage

/**
 * Represents the Deep Links to implicit navigate through the application, like PendingIntent.
 */
object DestinationDeeplink {

  private val baseUri = "app://${diceRollPackage}".toUri()

}