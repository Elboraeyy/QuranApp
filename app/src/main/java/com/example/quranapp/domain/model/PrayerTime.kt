package com.example.quranapp.domain.model

data class PrayerTime(
    val date: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
)

data class PrayerTimes(
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val method: Int,
    val times: List<PrayerTime>
)

