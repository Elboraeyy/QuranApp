package com.example.quranapp.domain.model

data class Tafsir(
    val id: String,
    val name: String,
    val nameArabic: String,
    val language: String
)

data class TafsirText(
    val surahNumber: Int,
    val ayahNumber: Int,
    val tafsirId: String,
    val text: String
)

