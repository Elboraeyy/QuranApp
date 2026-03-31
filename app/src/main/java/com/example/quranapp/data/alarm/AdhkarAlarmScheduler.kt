package com.example.quranapp.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.quranapp.data.alarm.AdhkarBroadcastReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdhkarAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAdhkarAlarms() {
        // ID 100: Morning, ID 101: Evening
        scheduleAlarm(6, 0, "أذكار الصباح", 100)
        scheduleAlarm(17, 0, "أذكار المساء", 101)
    }

    private fun scheduleAlarm(hour: Int, minute: Int, title: String, id: Int) {
        try {
            val now = LocalDateTime.now()
            var alarmTime = LocalDateTime.now()
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0)

            if (alarmTime.isBefore(now) || alarmTime.isEqual(now)) {
                alarmTime = alarmTime.plusDays(1)
            }

            val epochMillis = alarmTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val intent = Intent(context, AdhkarBroadcastReceiver::class.java).apply {
                putExtra("adhkar_title", title)
                putExtra("adhkar_id", id)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
            }
            Log.d("AdhkarAlarm", "Scheduled $title alarm at $alarmTime")
        } catch (e: Exception) {
            Log.e("AdhkarAlarm", "Failed to schedule $title: ${e.message}")
        }
    }
}
