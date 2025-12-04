package com.example.quranapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.quranapp.data.local.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM user_progress ORDER BY date DESC")
    fun getAllProgress(): Flow<List<ProgressEntity>>
    
    @Query("SELECT * FROM user_progress WHERE date = :date")
    suspend fun getProgressByDate(date: String): ProgressEntity?
    
    @Query("SELECT * FROM user_progress ORDER BY date DESC LIMIT 1")
    suspend fun getLatestProgress(): ProgressEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ProgressEntity)
    
    @Update
    suspend fun updateProgress(progress: ProgressEntity)
    
    @Query("SELECT * FROM user_progress ORDER BY date DESC")
    suspend fun getAllProgressList(): List<ProgressEntity>
}

