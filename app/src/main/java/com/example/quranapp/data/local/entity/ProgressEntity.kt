package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class ProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val pagesRead: Int = 0,
    val readingTimeMinutes: Int = 0,
    val ayahsRead: Int = 0,
    val lastReadSurah: Int? = null,
    val lastReadAyah: Int? = null,
    val lastReadPage: Int? = null
)

