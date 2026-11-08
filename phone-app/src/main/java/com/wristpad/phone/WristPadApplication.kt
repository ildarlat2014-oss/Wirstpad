package com.wristpad.phone

import android.app.Application
import android.content.Context
import androidx.core.app.NotificationCompat

class WristPadApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Create notification channel for Android O+
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        // Main channel for foreground service
        val mainChannel = NotificationCompat.Builder(this, "wristpad_channel")
            .setContentTitle("WristPad")
            .setContentText("Ready")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MEDIA)
            .build()

        // The NotificationManager will auto-create the channel if it doesn't exist,
        // but we explicitly create it here for clarity
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                android.app.NotificationChannel(
                    "wristpad_channel",
                    "WristPad Audio",
                    android.app.NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel for WristPad audio playback and notifications"
                }
            )
        }
    }
}