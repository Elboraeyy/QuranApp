package com.example.quranapp.domain.repository

import com.example.quranapp.domain.model.ReadingStreak
import com.example.quranapp.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    suspend fun saveProgress(progress: UserProgress)
    suspend fun getProgressByDate(date: String): UserProgress?
    suspend fun getTodayProgress(): UserProgress?
    suspend fun getAllProgress(): List<UserProgress>
    fun getProgressFlow(): Flow<List<UserProgress>>
    suspend fun getReadingStreak(): ReadingStreak
    suspend fun updateLastRead(surahNumber: Int, ayahNumber: Int, page: Int)
}

