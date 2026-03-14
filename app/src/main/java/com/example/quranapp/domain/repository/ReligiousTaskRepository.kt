package com.example.quranapp.domain.repository

import com.example.quranapp.data.local.entity.ReligiousTaskEntity
import com.example.quranapp.data.local.entity.TaskCompletionEntity
import com.example.quranapp.data.local.entity.TaskPeriod
import kotlinx.coroutines.flow.Flow

interface ReligiousTaskRepository {
    fun getAllTasks(): Flow<List<ReligiousTaskEntity>>
    fun getTasksByPeriod(period: TaskPeriod): Flow<List<ReligiousTaskEntity>>
    suspend fun insertTask(task: ReligiousTaskEntity): Long
    suspend fun deleteTask(task: ReligiousTaskEntity)
    fun getCompletionsByDate(date: String): Flow<List<TaskCompletionEntity>>
    suspend fun toggleCompletion(taskId: Long, date: String, progress: Int = 0)
    fun getTotalCompletionsCount(): Flow<Int>
}
