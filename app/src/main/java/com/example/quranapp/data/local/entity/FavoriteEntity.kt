package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.quranapp.domain.model.FavoriteType

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // FavoriteType as String
    val surahNumber: Int? = null,
    val ayahNumber: Int? = null,
    val reciterId: String? = null,
    val tafsirId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

