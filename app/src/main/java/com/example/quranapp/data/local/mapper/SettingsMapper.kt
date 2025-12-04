package com.example.quranapp.data.local.mapper

import com.example.quranapp.data.local.entity.SettingsEntity
import com.example.quranapp.domain.model.AppLanguage
import com.example.quranapp.domain.model.AppSettings
import com.example.quranapp.domain.model.FontFamily
import com.example.quranapp.domain.model.RepeatMode
import com.example.quranapp.domain.model.ThemeMode

fun SettingsEntity.toDomain(): AppSettings {
    return AppSettings(
        theme = ThemeMode.valueOf(theme),
        fontSize = fontSize,
        fontFamily = FontFamily.valueOf(fontFamily),
        selectedTranslation = selectedTranslation,
        selectedTafsir = selectedTafsir,
        selectedReciter = selectedReciter,
        audioSpeed = audioSpeed,
        repeatMode = RepeatMode.valueOf(repeatMode),
        appLanguage = AppLanguage.valueOf(appLanguage),
        enableNotifications = enableNotifications,
        enableAthan = enableAthan,
        calculationMethod = calculationMethod
    )
}

fun AppSettings.toEntity(): SettingsEntity {
    return SettingsEntity(
        theme = theme.name,
        fontSize = fontSize,
        fontFamily = fontFamily.name,
        selectedTranslation = selectedTranslation,
        selectedTafsir = selectedTafsir,
        selectedReciter = selectedReciter,
        audioSpeed = audioSpeed,
        repeatMode = repeatMode.name,
        appLanguage = appLanguage.name,
        enableNotifications = enableNotifications,
        enableAthan = enableAthan,
        calculationMethod = calculationMethod
    )
}

