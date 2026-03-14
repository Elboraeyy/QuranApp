package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val userId: String = "default_user", // Simple local user
    val totalPoints: Long = 0,
    val currentLevel: Int = 1,
    val currentXP: Long = 0,
    val nextLevelXP: Long = 1000,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val lastActiveDate: String? = null // YYYY-MM-DD
)
