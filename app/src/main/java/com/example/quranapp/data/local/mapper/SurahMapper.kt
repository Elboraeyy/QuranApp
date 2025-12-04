package com.example.quranapp.data.local.mapper

import com.example.quranapp.data.local.entity.SurahEntity
import com.example.quranapp.domain.model.Surah

fun SurahEntity.toDomain(): Surah {
    return Surah(
        number = number,
        name = name,
        nameArabic = nameArabic,
        nameEnglish = nameEnglish,
        englishNameTranslation = englishNameTranslation,
        revelationType = revelationType,
        numberOfAyahs = numberOfAyahs
    )
}

fun Surah.toEntity(): SurahEntity {
    return SurahEntity(
        number = number,
        name = name,
        nameArabic = nameArabic,
        nameEnglish = nameEnglish,
        englishNameTranslation = englishNameTranslation,
        revelationType = revelationType,
        numberOfAyahs = numberOfAyahs
    )
}

