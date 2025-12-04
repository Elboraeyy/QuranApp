package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ayahs",
    foreignKeys = [
        ForeignKey(
            entity = SurahEntity::class,
            parentColumns = ["number"],
            childColumns = ["surahNumber"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["surahNumber"]),
        Index(value = ["page"]),
        Index(value = ["juz"])
    ]
)
data class AyahEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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

