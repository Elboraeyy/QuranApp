package com.example.quranapp.data.local.mapper

import com.example.quranapp.data.local.entity.BookmarkEntity
import com.example.quranapp.domain.model.Bookmark

fun BookmarkEntity.toDomain(): Bookmark {
    return Bookmark(
        id = id,
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        page = page,
        juz = juz,
        createdAt = createdAt,
        note = note
    )
}

fun Bookmark.toEntity(): BookmarkEntity {
    return BookmarkEntity(
        id = id,
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        page = page,
        juz = juz,
        createdAt = createdAt,
        note = note
    )
}

