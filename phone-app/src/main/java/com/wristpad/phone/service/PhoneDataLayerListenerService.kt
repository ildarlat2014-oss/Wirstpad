package com.wristpad.phone.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService

class PhoneDataLayerListenerService : WearableListenerService() {

    private val TAG = "PhoneDataLayer"

    override fun onCreate() {
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: com.google.android.gms.wearable.MessageEvent) {
        super.onMessageReceived(remoteMessage)

        when (remoteMessage.path) {
            "/play_sound" -> {
                // Handle play sound command from watch
                val dataMap = remoteMessage.data
                val soundId = dataMap.getInt("sound_id", -1)
                val soundTitle = dataMap.getString("sound_title")

                // Play the sound
                playSoundFromId(soundId, soundTitle)

                // Send acknowledgment back
                sendPlayAck(soundTitle)
            }
            "/sync_library" -> {
                // Request library sync
                requestLibrarySync()
            }
            else -> {
                super.onMessageReceived(remoteMessage)
            }
        }
    }

    private fun playSoundFromId(soundId: Int, soundTitle: String?) {
        // Get sound from database and play via ExoPlayer
        // This would use the AudioService player
        Log.d(TAG, "Playing sound $soundId: $soundTitle")

        // Create intent to start/stay foreground service
        val intent = Intent(this, AudioService::class.java)
        intent.setAction("com.wristpad.phone.service.PLAY_SOUND")
        intent.putExtra("sound_id", soundId)
        intent.putExtra("sound_title", soundTitle)
        startForegroundService(intent)
    }

    private fun requestLibrarySync() {
        // Request full library sync from phone
        val intent = Intent("com.wristpad.phone.REQUEST_SYNC")
        sendBroadcast(intent)
    }

    private fun sendPlayAck(soundTitle: String?) {
        // Send acknowledgment via message path to watch
        Wearable.getClient(this).sendMessagePath(
            "/play_sound_ack",
            DataMap().apply { putString("title", soundTitle ?: "") }
        ).addOnSuccessListener {
            Log.d(TAG, "Acknowledgment sent to watch")
        }.addOnFailureListener {
            Log.e(TAG, "Failed to send acknowledgment")
        }
    }

    override fun onDataChanged(dataEvents: java.util.List<DataEvent>) {
        super.onDataChanged(dataEvents)

        for (event in dataEvents) {
            when (event.type) {
                DataEvent.TYPE_CHANGED -> {
                    val dataMap = event.dataMap
                    Log.d(TAG, "Data changed: ${dataMap.keys}")

                    // Handle sound updates from watch
                    val title = dataMap.getString("title")
                    if (title != null) {
                        updateSoundInDatabase(title, dataMap)
                    }
                }
                DataEvent.TYPE_CREATED -> {
                    // New sound added
                }
                DataEvent.TYPE_DELETED -> {
                    // Sound deleted
                }
            }
        }
    }

    private fun updateSoundInDatabase(title: String, dataMap: DataMap) {
        // Update or insert sound in Room database
        val volume = dataMap.getFloat("volume", 1.0f)
        val emoji = dataMap.getString("emoji") ?: ""
        val category = dataMap.getString("category") ?: ""

        // Run DB operation in coroutine
        // In production, would use proper ViewModel scope
        Thread {
            val db = SoundDatabase.getInstance(applicationContext)
            val dao = db.soundDao()

            // Check if sound exists
            val existing = dao.getAllSounds().firstOrNull { it.title == title }
            if (existing != null) {
                // Update existing
                // dao.update(existing.copy(volume = volume, emoji = emoji, category = category))
            } else {
                // Insert new
                // dao.insert(SoundEntity(title = title, emoji = emoji, category = category, volume = volume, filePath = "", createdAt = System.currentTimeMillis()))
            }
            Log.d(TAG, "Updated sound in database: $title")
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private val applicationContext: android.content.Context
        get() = this.applicationContext
}