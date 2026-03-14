package com.example.quranapp.data.local.dao

import androidx.room.*
import com.example.quranapp.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    fun getUserStats(userId: String = "default_user"): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: UserStatsEntity)

    @Query("UPDATE user_stats SET totalPoints = totalPoints + :points, currentXP = currentXP + :points WHERE userId = :userId")
    suspend fun addPoints(points: Int, userId: String = "default_user")
}
