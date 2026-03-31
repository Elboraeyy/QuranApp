package com.example.quranapp.data.repository

import com.example.quranapp.data.local.dao.ReligiousTaskDao
import com.example.quranapp.data.local.entity.ReligiousTaskEntity
import com.example.quranapp.data.local.entity.TaskCompletionEntity
import com.example.quranapp.data.local.entity.TaskPeriod
import com.example.quranapp.domain.repository.ReligiousTaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReligiousTaskRepositoryImpl @Inject constructor(
    private val taskDao: ReligiousTaskDao,
    private val userStatsDao: com.example.quranapp.data.local.dao.UserStatsDao
) : ReligiousTaskRepository {
    override fun getAllTasks(): Flow<List<ReligiousTaskEntity>> = taskDao.getAllTasks()

    override fun getTasksByPeriod(period: TaskPeriod): Flow<List<ReligiousTaskEntity>> =
        taskDao.getTasksByPeriod(period)

    override suspend fun insertTask(task: ReligiousTaskEntity): Long = taskDao.insertTask(task)

    override suspend fun deleteTask(task: ReligiousTaskEntity) = taskDao.deleteTask(task)

    override fun getCompletionsByDate(date: String): Flow<List<TaskCompletionEntity>> =
        taskDao.getCompletionsByDate(date)

    override suspend fun toggleCompletion(taskId: Long, date: String, progress: Int) {
        val task = taskDao.getTaskById(taskId) ?: return
        val existing = taskDao.getCompletion(taskId, date)
        
        if (existing == null) {
            // Mark as complete for the first time
            taskDao.insertCompletion(
                TaskCompletionEntity(
                    taskId = taskId,
                    date = date,
                    isCompleted = true,
                    currentProgress = progress,
                    pointsAwarded = task.points
                )
            )
            // Award points to user
            userStatsDao.addPoints(task.points)
        } else {
            val wasCompleted = existing.isCompleted
            val newCompleted = !wasCompleted
            
            taskDao.updateCompletion(
                existing.copy(
                    isCompleted = newCompleted,
                    currentProgress = if (newCompleted) progress else 0,
                    pointsAwarded = if (newCompleted) task.points else 0
                )
            )
            
            // Adjust user points
            if (newCompleted) {
                userStatsDao.addPoints(task.points)
            } else {
                userStatsDao.addPoints(-task.points) // Deduct points if uncompleted
            }
        }
    }

    override fun getTotalCompletionsCount(): Flow<Int> = taskDao.getTotalCompletionsCount()
}
