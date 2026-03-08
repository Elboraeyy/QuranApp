package com.example.quranapp.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.quranapp.domain.model.PrayerTime
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdhanAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarms(prayerTime: PrayerTime) {
        // Cancel all existing alarms first to avoid duplicates
        cancelAllAlarms()
        
        Log.d("AdhanAlarm", "Scheduling alarms for prayers: $prayerTime")

        // Helper to schedule individual prayer
        schedulePrayerAlarm(prayerTime.fajr, "الفجر", 1)
        schedulePrayerAlarm(prayerTime.sunrise, "الشروق", 2)
        schedulePrayerAlarm(prayerTime.dhuhr, "الظهر", 3)
        schedulePrayerAlarm(prayerTime.asr, "العصر", 4)
        schedulePrayerAlarm(prayerTime.maghrib, "المغرب", 5)
        schedulePrayerAlarm(prayerTime.isha, "العشاء", 6)
    }

    private fun schedulePrayerAlarm(timeString: String, prayerName: String, prayerId: Int) {
        try {
            // Time strings from Aladhan are like "05:00" or sometimes they contain timezone e.g. "05:00 (EET)"
            val cleanTime = timeString.substringBefore(" ").trim()
            
            val now = LocalDateTime.now()
            var alarmTime = LocalDateTime.now()
                .withHour(cleanTime.substringBefore(":").toInt())
                .withMinute(cleanTime.substringAfter(":").toInt())
                .withSecond(0)
                .withNano(0)

            // If time has already passed today, schedule for tomorrow
            if (alarmTime.isBefore(now) || alarmTime.isEqual(now)) {
                alarmTime = alarmTime.plusDays(1)
            }

            val epochMillis = alarmTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val intent = Intent(context, AdhanBroadcastReceiver::class.java).apply {
                putExtra("prayer_name", prayerName)
                putExtra("prayer_id", prayerId)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                prayerId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Check if exact alarms can be scheduled (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        epochMillis,
                        pendingIntent
                    )
                } else {
                    // Fallback to inexact repeating if exact is denied
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        epochMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    epochMillis,
                    pendingIntent
                )
            }
            Log.d("AdhanAlarm", "Scheduled $prayerName alarm at $alarmTime")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("AdhanAlarm", "Failed to schedule alarm for $prayerName: ${e.message}")
        }
    }

    fun cancelAllAlarms() {
        for (i in 1..6) {
            val intent = Intent(context, AdhanBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}
