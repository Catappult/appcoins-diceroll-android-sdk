package com.appcoins.diceroll.sdk.core.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.random.Random

class NotificationHandler @Inject constructor(
    @ApplicationContext
    val context: Context
) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationChannelID = context.packageName + "_notification_channel_id"

    init {
        val notificationChannel = NotificationChannel(
            notificationChannelID,
            "Notification name",
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    fun showPurchaseNotification(message: String) {
        val notification = NotificationCompat.Builder(context, notificationChannelID)
            .setContentTitle("Your Purchase has changed")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_diceroll)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }
}
