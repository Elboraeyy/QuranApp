package com.example.quranapp.data.local.mapper

import com.example.quranapp.data.local.entity.SettingsEntity
import com.example.quranapp.domain.model.AppLanguage
import com.example.quranapp.domain.model.AppSettings
import com.example.quranapp.domain.model.FontFamily
import com.example.quranapp.domain.model.RepeatMode
import com.example.quranapp.domain.model.ThemeMode
import com.example.quranapp.domain.model.AdhanPreference

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
        calculationMethod = calculationMethod,
        fajrPreference = AdhanPreference.valueOf(fajrPreference),
        sunrisePreference = AdhanPreference.valueOf(sunrisePreference),
        dhuhrPreference = AdhanPreference.valueOf(dhuhrPreference),
        asrPreference = AdhanPreference.valueOf(asrPreference),
        maghribPreference = AdhanPreference.valueOf(maghribPreference),
        ishaPreference = AdhanPreference.valueOf(ishaPreference),
        totalTasbihCount = totalTasbihCount,
        selectedTasbihPhraseId = selectedTasbihPhraseId,
        lastReadPage = lastReadPage
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
        calculationMethod = calculationMethod,
        fajrPreference = fajrPreference.name,
        sunrisePreference = sunrisePreference.name,
        dhuhrPreference = dhuhrPreference.name,
        asrPreference = asrPreference.name,
        maghribPreference = maghribPreference.name,
        ishaPreference = ishaPreference.name,
        totalTasbihCount = totalTasbihCount,
        selectedTasbihPhraseId = selectedTasbihPhraseId,
        lastReadPage = lastReadPage
    )
}

