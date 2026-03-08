package com.example.quranapp.data.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.quranapp.R
import com.example.quranapp.domain.repository.SettingsRepository
import com.example.quranapp.domain.model.AdhanPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.quranapp.MainActivity

@AndroidEntryPoint
class AdhanBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Need to reschedule alarms, but receiver shouldn't do long work.
            // A separate BootReceiver or a work manager is better for boot.
            return
        }

        val prayerName = intent.getStringExtra("prayer_name") ?: return
        val prayerId = intent.getIntExtra("prayer_id", 0)

        // Run in background to get settings
        CoroutineScope(Dispatchers.IO).launch {
            val settings = settingsRepository.getSettings()
            
            if (!settings.enableAthan || !settings.enableNotifications) return@launch

            val preference = when (prayerId) {
                1 -> settings.fajrPreference
                2 -> settings.sunrisePreference
                3 -> settings.dhuhrPreference
                4 -> settings.asrPreference
                5 -> settings.maghribPreference
                6 -> settings.ishaPreference
                else -> AdhanPreference.ADHAN
            }

            if (preference == AdhanPreference.NONE) return@launch

            showNotification(context, prayerName, prayerId, preference)
        }
    }

    private fun showNotification(context: Context, prayerName: String, prayerId: Int, preference: AdhanPreference) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Define channel ID based on preference because we can't change sound on existing channel easily
        val channelId = if (preference == AdhanPreference.ADHAN) "adhan_channel_full" else "adhan_channel_notification"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = if (preference == AdhanPreference.ADHAN) "Adhan Alerts" else "Silent Adhan Alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = "Notifications for prayer times"
                if (preference == AdhanPreference.ADHAN) {
                    val soundUri = Uri.parse("android.resource://${context.packageName}/raw/adhan_makkah")
                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                    setSound(soundUri, audioAttributes)
                }
            }
            notificationManager.createNotificationChannel(channel)
        }

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use your icon
            .setContentTitle("حان الآن موعد صلاة $prayerName")
            .setContentText("بتوقيت موقعك المحلي")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(prayerId, notification)
    }
}
