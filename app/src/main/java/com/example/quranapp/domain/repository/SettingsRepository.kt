package com.example.quranapp.domain.repository

import com.example.quranapp.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getSettings(): AppSettings
    suspend fun saveSettings(settings: AppSettings)
    fun getSettingsFlow(): Flow<AppSettings>
    suspend fun updateTheme(theme: com.example.quranapp.domain.model.ThemeMode)
    suspend fun updateFontSize(size: Float)
    suspend fun updateFontFamily(fontFamily: com.example.quranapp.domain.model.FontFamily)
    suspend fun updateSelectedTranslation(translationId: String?)
    suspend fun updateSelectedTafsir(tafsirId: String?)
    suspend fun updateSelectedReciter(reciterId: String)
    suspend fun clearCache()
}

