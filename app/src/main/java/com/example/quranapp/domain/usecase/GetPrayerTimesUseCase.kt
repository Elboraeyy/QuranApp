package com.example.quranapp.domain.usecase

import com.example.quranapp.domain.model.PrayerTime
import com.example.quranapp.domain.model.PrayerTimes
import com.example.quranapp.domain.repository.PrayerRepository
import javax.inject.Inject

class GetPrayerTimesUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        method: Int,
        month: Int,
        year: Int
    ): PrayerTimes {
        return repository.getPrayerTimes(latitude, longitude, method, month, year)
    }
}

class GetTodayPrayerTimeUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        method: Int
    ): PrayerTime {
        return repository.getTodayPrayerTime(latitude, longitude, method)
    }
}

class GetQiblaDirectionUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Double {
        return repository.getQiblaDirection(latitude, longitude)
    }
}

