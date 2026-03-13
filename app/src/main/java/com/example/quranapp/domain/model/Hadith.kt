package com.example.quranapp.domain.model

import com.google.gson.annotations.SerializedName

data class HadithBook(
    val id: Int,
    @SerializedName("title") val name: String,
    val author: String = "",
    val description: String = ""
)

data class HadithCategory(
    val id: Int,
    @SerializedName("title") val name: String,
    val iconId: String? = null
)

data class HadithVocab(
    val word: String,
    val meaning: String
)

data class Hadith(
    val id: Int,
    val bookId: Int,
    val categoryId: Int,
    val text: String = "",
    val narrator: String = "",
    val source: String = "",
    val explanation: String = "",
    val vocabulary: List<HadithVocab> = emptyList(),
    val lifeApplications: List<String> = emptyList()
)
