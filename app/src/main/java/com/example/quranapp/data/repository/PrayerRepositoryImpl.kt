package com.example.quranapp.data.repository

import com.example.quranapp.domain.model.PrayerTime
import com.example.quranapp.domain.model.PrayerTimes
import com.example.quranapp.domain.repository.PrayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

class PrayerRepositoryImpl @Inject constructor() : PrayerRepository {
    private val client = OkHttpClient()
    
    override suspend fun getPrayerTimes(
        latitude: Double,
        longitude: Double,
        method: Int,
        month: Int,
        year: Int
    ): PrayerTimes {
        return try {
            val url = "https://api.aladhan.com/v1/calendar?latitude=$latitude&longitude=$longitude&method=$method&month=$month&year=$year"
            val req = Request.Builder().url(url).build()
            client.newCall(req).execute().use { resp ->
                val body = resp.body?.string() ?: ""
                val root = JSONObject(body)
                val data = root.getJSONArray("data")
                val list = mutableListOf<PrayerTime>()
                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    val date = item.getJSONObject("date").getString("readable")
                    val t = item.getJSONObject("timings")
                    list.add(
                        PrayerTime(
                            date = date,
                            fajr = t.getString("Fajr"),
                            sunrise = t.getString("Sunrise"),
                            dhuhr = t.getString("Dhuhr"),
                            asr = t.getString("Asr"),
                            maghrib = t.getString("Maghrib"),
                            isha = t.getString("Isha")
                        )
                    )
                }
                PrayerTimes(
                    location = "",
                    latitude = latitude,
                    longitude = longitude,
                    timezone = "",
                    method = method,
                    times = list
                )
            }
        } catch (e: Exception) {
            PrayerTimes("", latitude, longitude, "", method, emptyList())
        }
    }
    
    override suspend fun getTodayPrayerTime(
        latitude: Double,
        longitude: Double,
        method: Int
    ): PrayerTime {
        return try {
            val url = "https://api.aladhan.com/v1/timings?latitude=$latitude&longitude=$longitude&method=$method"
            val req = Request.Builder().url(url).build()
            client.newCall(req).execute().use { resp ->
                val body = resp.body?.string() ?: ""
                val root = JSONObject(body)
                val data = root.getJSONObject("data")
                val date = data.getJSONObject("date").getString("readable")
                val t = data.getJSONObject("timings")
                PrayerTime(
                    date = date,
                    fajr = t.getString("Fajr"),
                    sunrise = t.getString("Sunrise"),
                    dhuhr = t.getString("Dhuhr"),
                    asr = t.getString("Asr"),
                    maghrib = t.getString("Maghrib"),
                    isha = t.getString("Isha")
                )
            }
        } catch (e: Exception) {
            PrayerTime("", "--:--", "--:--", "--:--", "--:--", "--:--", "--:--")
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
