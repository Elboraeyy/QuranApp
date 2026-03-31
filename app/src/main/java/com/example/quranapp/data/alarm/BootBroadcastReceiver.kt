package com.example.quranapp.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.quranapp.domain.repository.PrayerRepository
import com.example.quranapp.domain.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var adhanAlarmScheduler: AdhanAlarmScheduler

    @Inject
    lateinit var adhkarAlarmScheduler: AdhkarAlarmScheduler

    @Inject
    lateinit var prayerRepository: PrayerRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            val pendingResult = goAsync()
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // 1. Reschedule Adhkar (Constant times for now)
                    adhkarAlarmScheduler.scheduleAdhkarAlarms()
                    
                    // 2. Reschedule Adhan (Based on saved settings/location)
                    val settings = settingsRepository.getSettings()
                    if (settings.enableAthan) {
                        // Use saved location from settings (assuming it's stored there)
                        // For now we'll use a placeholder or the last fetched times
                        val prayerTime = prayerRepository.getTodayPrayerTime(
                            latitude = 30.0444, // Cairo default or from settings
                            longitude = 31.2357,
                            method = 5 // Egyptian General Authority
                        )
                        adhanAlarmScheduler.scheduleAlarms(prayerTime)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("BootReceiver", "Failed to reschedule alarms", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
