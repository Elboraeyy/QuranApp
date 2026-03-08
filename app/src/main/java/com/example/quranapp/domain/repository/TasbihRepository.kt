package com.example.quranapp.domain.repository

import com.example.quranapp.domain.model.ZikrPhrase
import kotlinx.coroutines.flow.Flow

interface TasbihRepository {
    fun getTotalCountFlow(): Flow<Int>
    suspend fun incrementTotalCount(amount: Int)
    suspend fun getStandardPhrases(): List<ZikrPhrase>
    fun getSelectedPhraseIdFlow(): Flow<Int>
    suspend fun saveSelectedPhraseId(id: Int)
}
