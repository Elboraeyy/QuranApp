package com.example.quranapp.domain.model

data class Translation(
    val id: String,
    val name: String,
    val language: String,
    val languageCode: String
)

data class TranslationText(
    val surahNumber: Int,
    val ayahNumber: Int,
    val translationId: String,
    val text: String
)

