package com.example.quranapp.data.remote.api

import com.example.quranapp.data.remote.dto.PrayerTimesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerApi {
    @GET("timings")
    suspend fun getPrayerTimings(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 5 // Egyptian General Authority of Survey
    ): PrayerTimesResponse
}
