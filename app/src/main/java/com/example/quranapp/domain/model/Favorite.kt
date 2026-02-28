package com.example.quranapp.domain.model

data class Favorite(
    val id: Long = 0,
    val type: FavoriteType,
    val surahNumber: Int? = null,
    val ayahNumber: Int? = null,
    val reciterId: String? = null,
    val tafsirId: String? = null,
    val adhkarId: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class FavoriteType {
    AYAH,
    SURAH,
    RECITER,
    TAFSIR,
    ADHKAR
}

