package com.example.quranapp.domain.repository

import com.example.quranapp.domain.model.Translation
import com.example.quranapp.domain.model.TranslationText
import kotlinx.coroutines.flow.Flow

interface TranslationRepository {
    suspend fun getAllTranslations(): List<Translation>
    suspend fun getTranslationById(id: String): Translation?
    suspend fun getTranslationText(surahNumber: Int, ayahNumber: Int, translationId: String): TranslationText?
    suspend fun getTranslationTextsBySurah(surahNumber: Int, translationId: String): List<TranslationText>
    suspend fun cacheTranslation(translationText: TranslationText)
}

