package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adhkar_categories")
data class AdhkarCategoryEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String
)
