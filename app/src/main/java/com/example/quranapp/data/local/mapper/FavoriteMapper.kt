package com.example.quranapp.data.local.mapper

import com.example.quranapp.data.local.entity.FavoriteEntity
import com.example.quranapp.domain.model.Favorite
import com.example.quranapp.domain.model.FavoriteType

fun FavoriteEntity.toDomain(): Favorite {
    return Favorite(
        id = id,
        type = FavoriteType.valueOf(type),
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        reciterId = reciterId,
        tafsirId = tafsirId,
        adhkarId = adhkarId,
        createdAt = createdAt
    )
}

fun Favorite.toEntity(): FavoriteEntity {
    return FavoriteEntity(
        id = id,
        type = type.name,
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        reciterId = reciterId,
        tafsirId = tafsirId,
        adhkarId = adhkarId,
        createdAt = createdAt
    )
}

