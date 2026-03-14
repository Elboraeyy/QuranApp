package com.example.quranapp.domain.repository

import com.example.quranapp.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

interface UserStatsRepository {
    fun getUserStats(userId: String = "default_user"): Flow<UserStatsEntity?>
    suspend fun updateStats(stats: UserStatsEntity)
    suspend fun addXP(points: Int)
    suspend fun calculateLevel(xp: Long): Int = (xp / 1000).toInt() + 1
}
