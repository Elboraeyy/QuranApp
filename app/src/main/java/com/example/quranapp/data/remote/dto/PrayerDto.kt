package com.example.quranapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PrayerTimesResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: PrayerDataDto
)

data class PrayerDataDto(
    @SerializedName("timings") val timings: PrayerTimingsDto,
    @SerializedName("date") val date: PrayerDateDto
)

data class PrayerTimingsDto(
    @SerializedName("Fajr") val fajr: String,
    @SerializedName("Sunrise") val sunrise: String,
    @SerializedName("Dhuhr") val dhuhr: String,
    @SerializedName("Asr") val asr: String,
    @SerializedName("Sunset") val sunset: String,
    @SerializedName("Maghrib") val maghrib: String,
    @SerializedName("Isha") val isha: String
)

data class PrayerDateDto(
    @SerializedName("readable") val readable: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("hijri") val hijri: HijriDateDto
)

data class HijriDateDto(
    @SerializedName("date") val date: String,
    @SerializedName("format") val format: String,
    @SerializedName("day") val day: String,
    @SerializedName("weekday") val weekday: HijriWeekdayDto,
    @SerializedName("month") val month: HijriMonthDto,
    @SerializedName("year") val year: String
)

data class HijriWeekdayDto(
    @SerializedName("en") val en: String,
    @SerializedName("ar") val ar: String
)

data class HijriMonthDto(
    @SerializedName("number") val number: Int,
    @SerializedName("en") val en: String,
    @SerializedName("ar") val ar: String
)
