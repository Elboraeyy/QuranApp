package com.example.quranapp.domain.repository

import com.example.quranapp.domain.model.TasbihItem
import kotlinx.coroutines.flow.Flow

interface TasbihRepository {
    fun getAllTasbihs(): Flow<List<TasbihItem>>
    suspend fun getTasbihById(id: Int): TasbihItem?
    suspend fun addTasbih(phrase: String, targetCount: Int)
    suspend fun updateTasbih(item: TasbihItem)
    suspend fun deleteTasbih(item: TasbihItem)
    suspend fun incrementTasbihCompletions(id: Int)
    fun getTotalLifetimeCompletions(): Flow<Int>
    suspend fun initializeDefaultTasbihsIfNeeded()
}
