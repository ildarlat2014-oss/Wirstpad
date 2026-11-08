package com.wristpad.phone.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.MediaItem
import androidx.media3.session.MediaSessionCompat

class AudioService : Service() {

    private var player: ExoPlayer? = null
    private var currentSoundId: Int = -1

    // Notification ID
    private const val NOTIFICATION_ID = 1001

    // Foreground service intent
    private val serviceIntent: Intent get() = Intent(this, AudioService::class.java)

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        player?.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        player?.prepare()

        // Setup MediaSession for playback controls
        val mediaSession = MediaSessionCompat(this, "WristPadAudioSession")
        mediaSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_ENABLED_DEFAULT
        )
        setMediaSession(mediaSession)

        // Start foreground service with notification
        startForeground(
            NOTIFICATION_ID,
            buildNotification("WristPad", "Ready", R.drawable.ic_launcher_foreground)
        )
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Handle intent actions
        val action = intent.action
        when (action) {
            -> {
                // Previous action
                player?.previous()
            }
            -> {
                // Play/pause action
                if (player?.currentState.isPlaying == true) {
                    player?.pause()
                } else {
                    player?.play()
                }
            }
            -> {
                // Next action
                player?.next()
            }
            -> {
                // Stop action
                stopSound()
            }
        }
        return START_STICKY
    }

    private fun playSound(sound: com.wristpad.phone.database.SoundEntity) {
        if (player != null) {
            val mediaItem = MediaItem.fromUri(sound.filePath)
            player?.setMediaItem(mediaItem)
            player?.play()
            currentSoundId = sound.id

            // Update notification with current track info
            val notification = buildNotification(
                "WristPad",
                "Now playing: ${sound.title}",
                R.drawable.ic_launcher_foreground
            ).setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(getMediaSession()))
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun stopSound() {
        player?.stop()
        player?.seekTo(0)
        currentSoundId = -1

        // Update notification to show ready state
        val notification = buildNotification("WristPad", "Ready", R.drawable.ic_launcher_foreground)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(title: String, text: String, smallIcon: Int): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, "wristpad_channel")
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(smallIcon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MEDIA)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_baseline_stop_24,
                    "Stop",
                    PendingIntent.getService(
                        this, 0,
                        Intent(this,::class.java).setAction("com.wristpad.phone.service.STOP"),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            )
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_baseline_play_arrow_24,
                    "Play",
                    PendingIntent.getService(
                        this, 0,
                        Intent(this,::class.java).setAction("com.wristpad.phone.service.PLAY"),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            )
    }

    private val getMediaSession: MediaSessionCompat.Session? {
        // Return the media session instance
        null // Simplified for example
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}