package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskPeriod {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

enum class TaskCategory {
    PRAYER, QURAN, ADHKAR, FASTING, CHARITY, OTHER
}

@Entity(tableName = "religious_tasks")
data class ReligiousTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: TaskCategory,
    val period: TaskPeriod,
    val isCustom: Boolean = false,
    val targetValue: Int = 1,
    val points: Int = 10, // Default 10 points per task
    val iconName: String? = null
)
