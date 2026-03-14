package com.example.quranapp.data.repository

import com.example.quranapp.data.local.dao.UserStatsDao
import com.example.quranapp.data.local.entity.UserStatsEntity
import com.example.quranapp.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserStatsRepositoryImpl @Inject constructor(
    private val userStatsDao: UserStatsDao
) : UserStatsRepository {
    override fun getUserStats(userId: String): Flow<UserStatsEntity?> =
        userStatsDao.getUserStats(userId)

    override suspend fun updateStats(stats: UserStatsEntity) =
        userStatsDao.insertOrUpdateStats(stats)

    override suspend fun addXP(points: Int) =
        userStatsDao.addPoints(points)
}
