package com.example.quranapp.data.repository

import com.example.quranapp.data.local.dao.ProgressDao
import com.example.quranapp.data.local.mapper.toDomain
import com.example.quranapp.data.local.mapper.toEntity
import com.example.quranapp.domain.model.ReadingStreak
import com.example.quranapp.domain.model.UserProgress
import com.example.quranapp.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val progressDao: ProgressDao
) : ProgressRepository {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    override suspend fun saveProgress(progress: UserProgress) {
        progressDao.insertProgress(progress.toEntity())
    }
    
    override suspend fun getProgressByDate(date: String): UserProgress? {
        return progressDao.getProgressByDate(date)?.toDomain()
    }
    
    override suspend fun getTodayProgress(): UserProgress? {
        val today = dateFormat.format(Calendar.getInstance().time)
        return getProgressByDate(today)
    }
    
    override suspend fun getAllProgress(): List<UserProgress> {
        return progressDao.getAllProgressList().map { it.toDomain() }
    }
    
    override fun getProgressFlow(): Flow<List<UserProgress>> {
        return progressDao.getAllProgress().map { progressList -> progressList.map { it.toDomain() } }
    }
    
    override suspend fun getReadingStreak(): ReadingStreak {
        val allProgress = getAllProgress()
        if (allProgress.isEmpty()) return ReadingStreak()
        
        val sortedProgress = allProgress.sortedByDescending { it.date }
        var currentStreak = 0
        var longestStreak = 0
        var tempStreak = 0
        var lastDate: String? = null
        
        val calendar = Calendar.getInstance()
        for (progress in sortedProgress) {
            if (lastDate == null) {
                lastDate = progress.date
                tempStreak = 1
            } else {
                val date1 = dateFormat.parse(progress.date)
                val date2 = dateFormat.parse(lastDate)
                if (date1 != null && date2 != null) {
                    val daysDiff = ((date2.time - date1.time) / (1000 * 60 * 60 * 24)).toInt()
                    if (daysDiff == 1) {
                        tempStreak++
                    } else {
                        longestStreak = maxOf(longestStreak, tempStreak)
                        tempStreak = 1
                    }
                }
            }
            lastDate = progress.date
        }
        
        longestStreak = maxOf(longestStreak, tempStreak)
        val today = dateFormat.format(calendar.time)
        val latestDate = sortedProgress.firstOrNull()?.date
        
        if (latestDate == today || latestDate == dateFormat.format(calendar.apply { add(Calendar.DAY_OF_YEAR, -1) }.time)) {
            currentStreak = tempStreak
        }
        
        return ReadingStreak(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            lastReadDate = latestDate
        )
    }
    
    override suspend fun updateLastRead(surahNumber: Int, ayahNumber: Int, page: Int) {
        val today = dateFormat.format(Calendar.getInstance().time)
        val existing = getProgressByDate(today)
        
        if (existing != null) {
            saveProgress(existing.copy(
                lastReadSurah = surahNumber,
                lastReadAyah = ayahNumber,
                lastReadPage = page
            ))
        } else {
            saveProgress(UserProgress(
                date = today,
                lastReadSurah = surahNumber,
                lastReadAyah = ayahNumber,
                lastReadPage = page
            ))
        }
    }
}

