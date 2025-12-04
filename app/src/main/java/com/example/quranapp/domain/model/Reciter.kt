package com.example.quranapp.domain.model

data class Reciter(
    val id: String,
    val name: String,
    val nameArabic: String,
    val server: String,
    val rewaya: String? = null
)

