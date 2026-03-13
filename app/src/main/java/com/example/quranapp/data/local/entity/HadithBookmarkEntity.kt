package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hadith_bookmarks")
data class HadithBookmarkEntity(
    @PrimaryKey val hadithId: Int,
    val bookId: Int,
    val categoryId: Int,
    val timestamp: Long = System.currentTimeMillis()
)
