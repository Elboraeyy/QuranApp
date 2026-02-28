package com.example.quranapp.domain.repository

import com.example.quranapp.domain.model.AdhkarCategory
import com.example.quranapp.domain.model.AdhkarItem
import kotlinx.coroutines.flow.Flow

interface AdhkarRepository {
    fun getCategories(): Flow<List<AdhkarCategory>>
    suspend fun getCategoryById(id: Int): AdhkarCategory?
    suspend fun updateAdhkarCount(adhkarId: Int, count: Int)
    suspend fun resetAllProgress()
}
