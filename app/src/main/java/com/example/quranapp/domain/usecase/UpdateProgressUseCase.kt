package com.example.quranapp.domain.usecase

import com.example.quranapp.domain.model.UserProgress
import com.example.quranapp.domain.repository.ProgressRepository
import javax.inject.Inject

class UpdateProgressUseCase @Inject constructor(
    private val repository: ProgressRepository
) {
    suspend operator fun invoke(progress: UserProgress) {
        repository.saveProgress(progress)
    }
}

class GetReadingStreakUseCase @Inject constructor(
    private val repository: ProgressRepository
) {
    suspend operator fun invoke() = repository.getReadingStreak()
}

class UpdateLastReadUseCase @Inject constructor(
    private val repository: ProgressRepository
) {
    suspend operator fun invoke(surahNumber: Int, ayahNumber: Int, page: Int) {
        repository.updateLastRead(surahNumber, ayahNumber, page)
    }
}

