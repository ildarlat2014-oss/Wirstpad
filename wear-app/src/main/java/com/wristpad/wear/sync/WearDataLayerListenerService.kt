package com.wristpad.wear.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.core.content.sendOrderedBroadcast
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService

class WearDataLayerListenerService : WearableListenerService() {

    private val TAG = "WearDataLayerListener"

    override fun onCreate() {
        super.onCreate()
        // Register receiver for data layer events
        registerReceiver()
    }

    private fun registerReceiver() {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {
                    handleDataEvent(intent)
                }
            }
        }

        val intentFilter = IntentFilter().apply {
            addAction(com.google.android.gms.wearable.DataMap.TEXT_CHANGED_ACTION)
            addAction(com.google.android.gms.wearable.DataMap.PATH_CHANGED_ACTION)
        }

        sendOrderedBroadcast(intent = null, receiver = receiver, scheduledExecutor = null)
        // Actually register the receiver properly
        registerReceiver(receiver, intentFilter)
    }

    private fun handleDataEvent(intent: Intent) {
        val dataEvent = DataEvent.createFromIntent(intent)
        when (dataEvent.type) {
            DataEvent.TYPE_CHANGED -> {
                val dataMap = dataEvent.dataMap
                Log.d(TAG, "Data map changed: $dataMap")

                // Extract sound data and update phone database
                val soundTitle = dataMap.getString("title")
                val soundEmoji = dataMap.getString("emoji")
                val soundCategory = dataMap.getString("category")
                val soundVolume = dataMap.getFloat("volume")

                if (soundTitle != null) {
                    // Send to phone via explicit intent
                    val phoneIntent = Intent("com.wristpad.phone.UPDATE_SOUND")
                        .putExtra("title", soundTitle)
                        .putExtra("emoji", soundEmoji)
                        .putExtra("category", soundCategory)
                        .putExtra("volume", soundVolume)
                    sendBroadcast(phoneIntent)
                }
            }
            DataEvent.TYPE_CREATED -> {
                // New item created on watch
                Log.d(TAG, "Data item created")
            }
            DataEvent.TYPE_DELETED -> {
                // Item deleted on watch
                Log.d(TAG, "Data item deleted")
            }
        }
    }

    // Receive updates from phone
    private val phoneUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                val action = intent.action
                when (action) {
                    "com.wristpad.phone.ADD_SOUND" -> {
                        // Add sound to watch's local cache or forward to other watches
                        val title = intent.getStringExtra("title")
                        val emoji = intent.getStringExtra("emoji")
                        val category = intent.getStringExtra("category")
                        val volume = intent.getFloatExtra("volume", 1.0f)

                        // Forward to Data Layer
                        sendSoundToDataLayer("/update_sound", title, emoji, category, volume)
                    }
                    "com.wristpad.phone.DELETE_SOUND" -> {
                        // Remove sound from local cache
                        val title = intent.getStringExtra("title")
                        Log.d(TAG, "Sound deleted: $title")
                    }
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: com.google.android.gms.wearable.MessageEvent) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received: ${remoteMessage.path}")

        when (remoteMessage.path) {
            "/play_sound" -> {
                // Handle play sound command from watch
                val dataMap = remoteMessage.data
                val soundTitle = dataMap.getString("sound_id")
                sendToPhone("/play_sound_response", mapOf("status" -> "ack", "title" -> soundTitle))
            }
            else -> {
                super.onMessageReceived(remoteMessage)
            }
        }
    }

    // Send message to phone via Data Layer
    private fun sendToPhone(path: String?, data: Map<String, Any?>) {
        Wearable.getClient(this).sendMessagePath(
            com.wristpad.wear.WearMainActivity.PATH_TO_PHONE,
            data
        ).addOnSuccessListener {
            Log.d(TAG, "Message sent to phone successfully")
        }.addOnFailureListener {
            Log.e(TAG, "Failed to send message to phone")
        }
    }

    // Send sound data to phone Data Layer
    private fun sendSoundToDataLayer(path: String?, title: String, emoji: String, category: String?, volume: Float) {
        val dataMap = DataMap().apply {
            putString("title", title)
            putString("emoji", emoji)
            putString("category", category ?: "")
            putFloat("volume", volume)
        }
        Wearable.getClient(this).sendMessagePath(
            "/update_sound",
            dataMap
        ).addOnSuccessListener {
            Log.d(TAG, "Sound data sent to phone: $title")
        }.addOnFailureListener {
            Log.e(TAG, "Failed to send sound data to phone")
        }
    }

    override fun onDestroy() {
        unregisterReceiver(phoneUpdateReceiver)
        super.onDestroy()
    }
}