package com.appcoins.diceroll.core.navigation.destinations

import androidx.core.net.toUri
import com.appcoins.diceroll.core.utils.diceRollPackage

/**
 * Represents the Deep Links to implicit navigate through the application, like PendingIntent.
 */
object DestinationDeeplink {

  private val baseUri = "app://${diceRollPackage}".toUri()

}