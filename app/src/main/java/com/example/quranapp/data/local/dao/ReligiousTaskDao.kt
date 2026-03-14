package com.example.quranapp.data.local.dao

import androidx.room.*
import com.example.quranapp.data.local.entity.ReligiousTaskEntity
import com.example.quranapp.data.local.entity.TaskCompletionEntity
import com.example.quranapp.data.local.entity.TaskPeriod
import kotlinx.coroutines.flow.Flow

@Dao
interface ReligiousTaskDao {
    @Query("SELECT * FROM religious_tasks")
    fun getAllTasks(): Flow<List<ReligiousTaskEntity>>

    @Query("SELECT * FROM religious_tasks WHERE period = :period")
    fun getTasksByPeriod(period: TaskPeriod): Flow<List<ReligiousTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ReligiousTaskEntity): Long

    @Delete
    suspend fun deleteTask(task: ReligiousTaskEntity)

    @Query("SELECT * FROM task_completions WHERE date = :date")
    fun getCompletionsByDate(date: String): Flow<List<TaskCompletionEntity>>

    @Query("SELECT * FROM task_completions WHERE taskId = :taskId AND date = :date")
    suspend fun getCompletion(taskId: Long, date: String): TaskCompletionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: TaskCompletionEntity)

    @Update
    suspend fun updateCompletion(completion: TaskCompletionEntity)
    
    @Query("SELECT COUNT(*) FROM task_completions WHERE isCompleted = 1")
    fun getTotalCompletionsCount(): Flow<Int>
}
