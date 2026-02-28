package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adhkar_progress")
data class AdhkarProgressEntity(
    @PrimaryKey val adhkarId: Int,
    val currentCount: Int,
    val lastUpdatedDate: String // YYYY-MM-DD to reset daily if needed
)
