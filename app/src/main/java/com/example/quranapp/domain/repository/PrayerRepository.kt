package com.example.quranapp.domain.repository

import com.example.quranapp.domain.model.PrayerTime
import com.example.quranapp.domain.model.PrayerTimes
import kotlinx.coroutines.flow.Flow

interface PrayerRepository {
    suspend fun getPrayerTimes(
        latitude: Double,
        longitude: Double,
        method: Int,
        month: Int,
        year: Int
    ): PrayerTimes
    
    suspend fun getTodayPrayerTime(
        latitude: Double,
        longitude: Double,
        method: Int
    ): PrayerTime
    
    suspend fun getQiblaDirection(latitude: Double, longitude: Double): Double
    fun getPrayerTimesFlow(): Flow<PrayerTime>
}

