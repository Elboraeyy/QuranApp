package com.example.quranapp.data.repository

import com.example.quranapp.data.local.dao.SettingsDao
import com.example.quranapp.data.local.mapper.toDomain
import com.example.quranapp.data.local.mapper.toEntity
import com.example.quranapp.domain.model.AppSettings
import com.example.quranapp.domain.model.FontFamily
import com.example.quranapp.domain.model.ThemeMode
import com.example.quranapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {
    
    override suspend fun getSettings(): AppSettings {
        return settingsDao.getSettingsOnce()?.toDomain() ?: AppSettings()
    }
    
    override suspend fun saveSettings(settings: AppSettings) {
        val entity = settings.toEntity()
        settingsDao.insertSettings(entity)
    }
    
    override fun getSettingsFlow(): Flow<AppSettings> {
        return settingsDao.getSettings().map { it?.toDomain() ?: AppSettings() }
    }
    
    override suspend fun updateTheme(theme: ThemeMode) {
        val current = getSettings()
        saveSettings(current.copy(theme = theme))
    }
    
    override suspend fun updateFontSize(size: Float) {
        val current = getSettings()
        saveSettings(current.copy(fontSize = size))
    }
    
    override suspend fun updateFontFamily(fontFamily: FontFamily) {
        val current = getSettings()
        saveSettings(current.copy(fontFamily = fontFamily))
    }
    
    override suspend fun updateSelectedTranslation(translationId: String?) {
        val current = getSettings()
        saveSettings(current.copy(selectedTranslation = translationId))
    }
    
    override suspend fun updateSelectedTafsir(tafsirId: String?) {
        val current = getSettings()
        saveSettings(current.copy(selectedTafsir = tafsirId))
    }
    
    override suspend fun updateSelectedReciter(reciterId: String) {
        val current = getSettings()
        saveSettings(current.copy(selectedReciter = reciterId))
    }
    
    override suspend fun clearCache() {
        // Implementation for clearing cache
        // This can be extended to clear downloaded files, etc.
    }
}

