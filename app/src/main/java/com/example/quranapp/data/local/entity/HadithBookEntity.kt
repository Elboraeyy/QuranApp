package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hadith_books")
data class HadithBookEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val author: String,
    val description: String
)
