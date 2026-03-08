package com.example.quranapp.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.quranapp.MainActivity
import com.example.quranapp.R
import com.example.quranapp.domain.manager.AudioPlayerManager
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerService : Service() {

    @Inject
    lateinit var audioPlayerManager: AudioPlayerManager

    private lateinit var notificationManager: PlayerNotificationManager
    private lateinit var mediaSession: MediaSessionCompat

    private var isForegroundService = false

    override fun onCreate() {
        super.onCreate()

        // Create a Media Session
        mediaSession = MediaSessionCompat(this, "AudioPlayerService")
        mediaSession.isActive = true

        // Build Notification Manager
        val channelId = "quran_audio_channel"
        val notificationId = 111

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val descriptionAdapter = object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): CharSequence {
                val surahNumber = audioPlayerManager.currentSurah.value
                return "سورة رقم $surahNumber"
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                return pendingIntent
            }

            override fun getCurrentContentText(player: Player): CharSequence {
                return "تطبيق القرآن الكريم"
            }

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                return null
            }
        }

        notificationManager = PlayerNotificationManager.Builder(this, notificationId, channelId)
            .setChannelNameResourceId(R.string.app_name) // Using app name as channel name for simplicity
            .setChannelDescriptionResourceId(R.string.app_name)
            .setMediaDescriptionAdapter(descriptionAdapter)
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                    stopForeground(true)
                    isForegroundService = false
                    stopSelf()
                }

                override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
                    if (ongoing && !isForegroundService) {
                        try {
                            startForeground(notificationId, notification)
                            isForegroundService = true
                        } catch (e: Exception) {
                            // Ignore foreground service exceptions if app is in background
                        }
                    }
                }
            })
            .build()
            
        notificationManager.setUseNextAction(true)
        notificationManager.setUsePreviousAction(true)
        notificationManager.setUseNextActionInCompactView(true)
        notificationManager.setUsePreviousActionInCompactView(true)

        notificationManager.setMediaSessionToken(mediaSession.sessionToken)

        // Attach ExoPlayer
        notificationManager.setPlayer(audioPlayerManager.player)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        notificationManager.setPlayer(null)
        mediaSession.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

