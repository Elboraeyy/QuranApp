package com.example.quranapp.data.remote.api

import com.example.quranapp.data.remote.dto.*
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerApi {
    @GET("timings")
    suspend fun getPrayerTimings(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 5 // Egyptian General Authority of Survey
    ): PrayerTimesResponse

    @GET("calendar")
    suspend fun getCalendar(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Query("method") method: Int = 5
    ): PrayerCalendarResponse
}

data class PrayerCalendarResponse(
    val code: Int,
    val status: String,
    val data: List<PrayerDayData>
)

data class PrayerDayData(
    @SerializedName("timings") val timings: PrayerTimingsDto,
    @SerializedName("date") val date: PrayerDateDto
)
