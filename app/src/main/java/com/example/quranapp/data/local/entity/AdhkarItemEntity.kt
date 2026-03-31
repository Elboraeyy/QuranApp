package com.example.quranapp.data.local.entity

import androidx.room.*

@Entity(
    tableName = "adhkar_items",
    foreignKeys = [
        ForeignKey(
            entity = AdhkarCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class AdhkarItemEntity(
    @PrimaryKey val id: Int,
    val categoryId: Int,
    val text: String,
    val targetCount: Int,
    val reference: String
)
