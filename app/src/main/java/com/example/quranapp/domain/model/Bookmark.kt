package com.example.quranapp.domain.model

data class Bookmark(
    val id: Long = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val page: Int,
    val juz: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val note: String? = null
)

