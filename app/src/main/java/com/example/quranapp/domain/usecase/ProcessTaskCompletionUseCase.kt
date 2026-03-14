package com.example.quranapp.domain.usecase

import com.example.quranapp.domain.repository.ReligiousTaskRepository
import com.example.quranapp.domain.repository.UserStatsRepository
import com.example.quranapp.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ProcessTaskCompletionUseCase @Inject constructor(
    private val taskRepository: ReligiousTaskRepository,
    private val statsRepository: UserStatsRepository
) {
    suspend operator fun invoke(taskId: Long, date: String) {
        val tasks = taskRepository.getAllTasks().first()
        val task = tasks.find { it.id == taskId } ?: return
        
        // Toggle completion in DB
        taskRepository.toggleCompletion(taskId, date)
        
        // Get completion status after toggle
        val completions = taskRepository.getCompletionsByDate(date).first()
        val isCompleted = completions.find { it.taskId == taskId }?.isCompleted == true
        
        if (isCompleted) {
            // Award points
            statsRepository.addXP(task.points)
            updateStreak(date)
        }
    }

    private suspend fun updateStreak(today: String) {
        val stats = statsRepository.getUserStats().first() ?: UserStatsEntity()
        val lastActive = stats.lastActiveDate
        
        if (lastActive == today) return // Already updated today
        
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val todayDate = sdf.parse(today)
        
        val newStreak = if (lastActive != null) {
            val lastDate = sdf.parse(lastActive)
            val diff = (todayDate.time - lastDate.time) / (1000 * 60 * 60 * 24)
            
            when {
                diff == 1L -> stats.currentStreak + 1
                diff > 1L -> 1
                else -> stats.currentStreak
            }
        } else {
            1
        }

        val newMaxStreak = if (newStreak > stats.maxStreak) newStreak else stats.maxStreak
        
        // Update level if needed (simple linear model: Level = XP / 1000 + 1)
        val newXP = stats.currentXP + 10 // This is a bit redundant if we use addXP, let's refine
        // I'll handle level calc based on total XP
        
        statsRepository.updateStats(stats.copy(
            currentStreak = newStreak,
            maxStreak = newMaxStreak,
            lastActiveDate = today
        ))
    }
}
