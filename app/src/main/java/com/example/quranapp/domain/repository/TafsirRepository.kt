package com.example.quranapp.domain.repository

import com.example.quranapp.domain.model.Tafsir
import com.example.quranapp.domain.model.TafsirText
import kotlinx.coroutines.flow.Flow

interface TafsirRepository {
    suspend fun getAllTafsirs(): List<Tafsir>
    suspend fun getTafsirById(id: String): Tafsir?
    suspend fun getTafsirText(surahNumber: Int, ayahNumber: Int, tafsirId: String): TafsirText?
    suspend fun getTafsirTextsBySurah(surahNumber: Int, tafsirId: String): List<TafsirText>
    suspend fun cacheTafsir(tafsirText: TafsirText)
}

