package com.example.quranapp.domain.model

data class Ayah(
    val number: Int,
    val numberInSurah: Int,
    val surahNumber: Int,
    val text: String,
    val textUthmani: String,
    val juz: Int,
    val hizb: Int,
    val manzil: Int,
    val ruku: Int,
    val page: Int,
    val sajda: Boolean = false
)

