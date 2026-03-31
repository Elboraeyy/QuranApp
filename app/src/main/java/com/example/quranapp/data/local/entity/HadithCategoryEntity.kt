package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hadith_categories")
data class HadithCategoryEntity(
    @PrimaryKey val id: Int,
    val title: String
)
