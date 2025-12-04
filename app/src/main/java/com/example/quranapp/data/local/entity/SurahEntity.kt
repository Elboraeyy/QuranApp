package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val number: Int,
    val name: String,
    val nameArabic: String,
    val nameEnglish: String,
    val englishNameTranslation: String,
    val revelationType: String,
    val numberOfAyahs: Int
)

