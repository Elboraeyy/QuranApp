package com.example.quranapp.domain.model

data class UserProgress(
    val id: Long = 0,
    val date: String,
    val pagesRead: Int = 0,
    val readingTimeMinutes: Int = 0,
    val ayahsRead: Int = 0,
    val lastReadSurah: Int? = null,
    val lastReadAyah: Int? = null,
    val lastReadPage: Int? = null
)

data class ReadingStreak(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastReadDate: String? = null
)

