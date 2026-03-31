package com.example.quranapp.data.local.dao

import androidx.room.*
import com.example.quranapp.data.local.entity.UserStatsEntity
import com.example.quranapp.data.local.entity.ReligiousTaskEntity
import com.example.quranapp.data.local.entity.HadithEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {
    @Query("SELECT COUNT(*) FROM task_completions WHERE isCompleted = 1")
    fun getTotalCompletionsCount(): Flow<Int>

    @Query("SELECT * FROM religious_tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): ReligiousTaskEntity?

    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    fun getUserStats(userId: String = "default_user"): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: UserStatsEntity)

    @Query("UPDATE user_stats SET totalPoints = totalPoints + :points, currentXP = currentXP + :points WHERE userId = :userId")
    suspend fun addPoints(points: Int, userId: String = "default_user")

    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    suspend fun getUserStatsSnapshot(userId: String = "default_user"): UserStatsEntity?
}
