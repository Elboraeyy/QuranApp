package com.example.quranapp.domain.model

data class AdhkarCategory(
    val id: Int,
    val title: String,
    val description: String,
    val items: List<AdhkarItem>,
    val progress: Int = 0,
    val total: Int = items.size
)

data class AdhkarItem(
    val id: Int,
    val text: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val reference: String = ""
)
