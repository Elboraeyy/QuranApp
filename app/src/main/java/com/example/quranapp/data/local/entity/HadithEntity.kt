package com.example.quranapp.data.local.entity

import androidx.room.*
import com.example.quranapp.domain.model.HadithVocab

@Entity(
    tableName = "hadiths",
    foreignKeys = [
        ForeignKey(
            entity = HadithBookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HadithCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["bookId"]),
        Index(value = ["categoryId"])
    ]
)
data class HadithEntity(
    @PrimaryKey val id: Int,
    val bookId: Int,
    val categoryId: Int,
    val narrator: String,
    val text: String,
    val source: String,
    val explanation: String,
    val vocabulary: List<HadithVocab>,
    val lifeApplications: List<String>
)
