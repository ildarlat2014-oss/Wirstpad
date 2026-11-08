package com.wristpad.phone.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

// Broadcast receiver for Bluetooth connection state changes
class BluetoothStateReceiver : BroadcastReceiver() {
    private val TAG = "BluetoothStateReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            when (action) {
                "com.wristpad.phone.BLUETOOTH_CONNECTED" -> {
                    Log.d(TAG, "Bluetooth connected")
                    // Show connection status on watch
                    showConnectionStatus(context, true)
                }
                "com.wristpad.phone.BLUETOOTH_DISCONNECTED" -> {
                    Log.d(TAG, "Bluetooth disconnected")
                    // Show connection status on watch
                    showConnectionStatus(context, false)
                    // Foreground service notification updates
                    val notification = NotificationCompat.Builder(context, "wristpad_channel")
                        .setContentTitle("WristPad")
                        .setContentText("Disconnected from hours")
                        .setSmallIcon(R.drawable.ic_baseline_bluetooth_off_24)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .build()
                    context.getSystemService(NotificationManagerCompat::class.java).notify(1001, notification)
                }
                "com.wristpad.phone.BLUETOOTH_ERROR" -> {
                    Log.e(TAG, "Bluetooth error")
                }
            }
        }
    }

    private fun showConnectionStatus(context: Context, connected: Boolean) {
        val text = if (connected) "Connected" else "Disconnected"
        val color = if (connected) Color.GREEN else Color.RED

        val notification = NotificationCompat.Builder(context, "wristpad_channel")
            .setContentTitle("WristPad")
            .setContentText(text)
            .setSmallIcon(if (connected) R.drawable.ic_baseline_bluetooth_connected_24 else R.drawable.ic_baseline_bluetooth_off_24)
            .setColor(color)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        context.getSystemService(NotificationManagerCompat::class.java).notify(1002, notification)
    }
}