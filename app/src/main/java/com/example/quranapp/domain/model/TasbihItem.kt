package com.example.quranapp.domain.model

data class TasbihItem(
    val id: Int = 0,
    val phrase: String,
    val targetCount: Int,
    val totalCompletions: Int = 0,
    val isDefault: Boolean = false
)
