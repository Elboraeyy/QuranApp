package com.example.quranapp.data.repository

import com.example.quranapp.data.local.dao.ReligiousTaskDao
import com.example.quranapp.data.local.entity.ReligiousTaskEntity
import com.example.quranapp.data.local.entity.TaskCompletionEntity
import com.example.quranapp.data.local.entity.TaskPeriod
import com.example.quranapp.domain.repository.ReligiousTaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReligiousTaskRepositoryImpl @Inject constructor(
    private val taskDao: ReligiousTaskDao
) : ReligiousTaskRepository {
    override fun getAllTasks(): Flow<List<ReligiousTaskEntity>> = taskDao.getAllTasks()

    override fun getTasksByPeriod(period: TaskPeriod): Flow<List<ReligiousTaskEntity>> =
        taskDao.getTasksByPeriod(period)

    override suspend fun insertTask(task: ReligiousTaskEntity): Long = taskDao.insertTask(task)

    override suspend fun deleteTask(task: ReligiousTaskEntity) = taskDao.deleteTask(task)

    override fun getCompletionsByDate(date: String): Flow<List<TaskCompletionEntity>> =
        taskDao.getCompletionsByDate(date)

    override suspend fun toggleCompletion(taskId: Long, date: String, progress: Int) {
        val existing = taskDao.getCompletion(taskId, date)
        if (existing == null) {
            taskDao.insertCompletion(
                TaskCompletionEntity(
                    taskId = taskId,
                    date = date,
                    isCompleted = true,
                    currentProgress = progress
                )
            )
        } else {
            taskDao.updateCompletion(
                existing.copy(
                    isCompleted = !existing.isCompleted,
                    currentProgress = if (!existing.isCompleted) progress else 0
                )
            )
        }
    }

    override fun getTotalCompletionsCount(): Flow<Int> = taskDao.getTotalCompletionsCount()
}
