package com.example.quranapp.data.local.mapper

import com.example.quranapp.data.local.entity.AyahEntity
import com.example.quranapp.domain.model.Ayah

fun AyahEntity.toDomain(): Ayah {
    return Ayah(
        number = number,
        numberInSurah = numberInSurah,
        surahNumber = surahNumber,
        text = text,
        textUthmani = textUthmani,
        juz = juz,
        hizb = hizb,
        manzil = manzil,
        ruku = ruku,
        page = page,
        sajda = sajda
    )
}

fun Ayah.toEntity(): AyahEntity {
    return AyahEntity(
        number = number,
        numberInSurah = numberInSurah,
        surahNumber = surahNumber,
        text = text,
        textUthmani = textUthmani,
        juz = juz,
        hizb = hizb,
        manzil = manzil,
        ruku = ruku,
        page = page,
        sajda = sajda
    )
}

