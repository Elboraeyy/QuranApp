package com.example.quranapp.domain.model

data class AppSettings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val fontSize: Float = 18f,
    val fontFamily: FontFamily = FontFamily.UTHMANI,
    val selectedTranslation: String? = null,
    val selectedTafsir: String? = null,
    val selectedReciter: String = "abdul_basit",
    val audioSpeed: Float = 1.0f,
    val repeatMode: RepeatMode = RepeatMode.NONE,
    val appLanguage: AppLanguage = AppLanguage.ENGLISH,
    val enableNotifications: Boolean = true,
    val enableAthan: Boolean = true,
    val calculationMethod: Int = 1
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class FontFamily {
    UTHMANI,
    INDO_PAK,
    QALAM_MAJEED,
    NOOR_HAYAH,
    KFGQPC
}

enum class RepeatMode {
    NONE,
    REPEAT_AYAH,
    REPEAT_SURAH
}

enum class AppLanguage {
    ENGLISH,
    ARABIC,
    URDU,
    FRENCH,
    MALAY,
    INDONESIAN
}

