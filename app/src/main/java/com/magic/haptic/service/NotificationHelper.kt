package com.magic.haptic.service

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import com.magic.haptic.MagicApp

class NotificationHelper(private val context: Context) {
    fun buildNotification(
        title: String = "System Optimizer",
        body: String = "Running...",
    ): Notification {
        return NotificationCompat.Builder(context, MagicApp.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_menu_manage) // Default icon, should be swapped later
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
}
