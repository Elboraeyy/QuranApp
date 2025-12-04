package com.example.quranapp.data.local.mapper

import com.example.quranapp.data.local.entity.ProgressEntity
import com.example.quranapp.domain.model.UserProgress

fun ProgressEntity.toDomain(): UserProgress {
    return UserProgress(
        id = id,
        date = date,
        pagesRead = pagesRead,
        readingTimeMinutes = readingTimeMinutes,
        ayahsRead = ayahsRead,
        lastReadSurah = lastReadSurah,
        lastReadAyah = lastReadAyah,
        lastReadPage = lastReadPage
    )
}

fun UserProgress.toEntity(): ProgressEntity {
    return ProgressEntity(
        id = id,
        date = date,
        pagesRead = pagesRead,
        readingTimeMinutes = readingTimeMinutes,
        ayahsRead = ayahsRead,
        lastReadSurah = lastReadSurah,
        lastReadAyah = lastReadAyah,
        lastReadPage = lastReadPage
    )
}

