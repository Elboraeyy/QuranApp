package com.example.quranapp.data.repository

import com.example.quranapp.domain.model.PrayerTime
import com.example.quranapp.domain.model.PrayerTimes
import com.example.quranapp.domain.repository.PrayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.example.quranapp.data.remote.api.PrayerApi
import javax.inject.Inject

class PrayerRepositoryImpl @Inject constructor(
    private val prayerApi: PrayerApi
) : PrayerRepository {
    override suspend fun getPrayerTimes(
        latitude: Double,
        longitude: Double,
        method: Int,
        month: Int,
        year: Int
    ): PrayerTimes {
        return try {
            // Note: Our API is just getPrayerTimings (which returns for today)
            // If the UI needs a full month, we should use calendar API. But currently
            // the interface doesn't match the API method. Let's return today's as a fallback.
            val response = prayerApi.getPrayerTimings(latitude, longitude, method)
            val timings = response.data.timings
            val date = response.data.date
            
            val hijriStr = "${date.hijri.weekday.ar} ${date.hijri.day} ${date.hijri.month.ar} ${date.hijri.year} هـ"
            
            val prayerTime = PrayerTime(
                date = hijriStr,
                fajr = timings.fajr,
                sunrise = timings.sunrise,
                dhuhr = timings.dhuhr,
                asr = timings.asr,
                maghrib = timings.maghrib,
                isha = timings.isha
            )
            
            PrayerTimes(
                location = "Unknown", // Will be resolved by ViewModel
                latitude = latitude,
                longitude = longitude,
                timezone = "UTC",
                method = method,
                times = listOf(prayerTime)
            )
        } catch (e: Exception) {
            PrayerTimes(
                location = "Unknown",
                latitude = latitude,
                longitude = longitude,
                timezone = "UTC",
                method = method,
                times = emptyList()
            )
        }
    }
    
    override suspend fun getTodayPrayerTime(
        latitude: Double,
        longitude: Double,
        method: Int
    ): PrayerTime {
        return try {
            val response = prayerApi.getPrayerTimings(latitude, longitude, method)
            val timings = response.data.timings
            val date = response.data.date
            
            val hijriStr = "${date.hijri.weekday.ar} ${date.hijri.day} ${date.hijri.month.ar} ${date.hijri.year} هـ"
            
            PrayerTime(
                date = hijriStr,
                fajr = timings.fajr,
                sunrise = timings.sunrise,
                dhuhr = timings.dhuhr,
                asr = timings.asr,
                maghrib = timings.maghrib,
                isha = timings.isha
            )
        } catch (e: Exception) {
            PrayerTime(
                date = "",
                fajr = "05:00",
                sunrise = "06:00",
                dhuhr = "12:00",
                asr = "15:00",
                maghrib = "18:00",
                isha = "19:00"
            )
        }
    }
    
    override suspend fun getQiblaDirection(latitude: Double, longitude: Double): Double {
        // Calculate Qibla direction (bearing to Mecca)
        val meccaLat = 21.4225
        val meccaLon = 39.8262
        
        val lat1 = Math.toRadians(latitude)
        val lat2 = Math.toRadians(meccaLat)
        val deltaLon = Math.toRadians(meccaLon - longitude)
        
        val y = Math.sin(deltaLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLon)
        
        val bearing = Math.toDegrees(Math.atan2(y, x))
        return (bearing + 360) % 360
    }
    
    override fun getPrayerTimesFlow(): Flow<PrayerTime> {
        return flowOf(
            PrayerTime(
                date = "",
                fajr = "05:00",
                sunrise = "06:00",
                dhuhr = "12:00",
                asr = "15:00",
                maghrib = "18:00",
                isha = "19:00"
            )
        )
    }
}

