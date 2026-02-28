package com.example.quranapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quranapp.data.local.entity.AdhkarProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdhkarDao {
    @Query("SELECT * FROM adhkar_progress")
    fun getAllProgress(): Flow<List<AdhkarProgressEntity>>

    @Query("SELECT * FROM adhkar_progress")
    suspend fun getAllProgressList(): List<AdhkarProgressEntity>

    @Query("SELECT * FROM adhkar_progress WHERE adhkarId = :adhkarId")
    suspend fun getProgressForId(adhkarId: Int): AdhkarProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: AdhkarProgressEntity)

    @Query("UPDATE adhkar_progress SET currentCount = 0 WHERE lastUpdatedDate != :today")
    suspend fun resetOldProgress(today: String)

    @Query("DELETE FROM adhkar_progress")
    suspend fun clearAllProgress()
}
