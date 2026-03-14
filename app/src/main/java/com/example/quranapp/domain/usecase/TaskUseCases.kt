package com.example.quranapp.domain.usecase

import com.example.quranapp.data.local.entity.ReligiousTaskEntity
import com.example.quranapp.data.local.entity.TaskCompletionEntity
import com.example.quranapp.data.local.entity.TaskPeriod
import com.example.quranapp.domain.repository.ReligiousTaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: ReligiousTaskRepository
) {
    operator fun invoke(period: TaskPeriod): Flow<List<ReligiousTaskEntity>> =
        repository.getTasksByPeriod(period)
}

class ToggleTaskCompletionUseCase @Inject constructor(
    private val repository: ReligiousTaskRepository
) {
    suspend operator fun invoke(taskId: Long, date: String, progress: Int = 0) =
        repository.toggleCompletion(taskId, date, progress)
}

class GetCompletionsUseCase @Inject constructor(
    private val repository: ReligiousTaskRepository
) {
    operator fun invoke(date: String): Flow<List<TaskCompletionEntity>> =
        repository.getCompletionsByDate(date)
}
