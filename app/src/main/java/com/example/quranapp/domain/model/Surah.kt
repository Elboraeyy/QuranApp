package com.example.quranapp.domain.model

data class Surah(
    val number: Int,
    val name: String,
    val nameArabic: String,
    val nameEnglish: String,
    val englishNameTranslation: String,
    val revelationType: String, // "Meccan" or "Medinan"
    val numberOfAyahs: Int,
    val ayahs: List<Ayah> = emptyList()
)
