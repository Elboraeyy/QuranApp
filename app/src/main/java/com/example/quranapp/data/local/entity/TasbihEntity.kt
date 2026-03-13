package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasbih")
data class TasbihEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phrase: String,
    val targetCount: Int,
    val totalCompletions: Int = 0,
    val isDefault: Boolean = false
)
