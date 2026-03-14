package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_completions",
    foreignKeys = [
        ForeignKey(
            entity = ReligiousTaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskId", "date"], unique = true)]
)
data class TaskCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val date: String, // YYYY-MM-DD
    val isCompleted: Boolean,
    val currentProgress: Int = 0,
    val pointsAwarded: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
