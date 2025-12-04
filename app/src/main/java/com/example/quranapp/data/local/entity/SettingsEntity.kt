package com.example.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val theme: String = "SYSTEM",
    val fontSize: Float = 18f,
    val fontFamily: String = "UTHMANI",
    val selectedTranslation: String? = null,
    val selectedTafsir: String? = null,
    val selectedReciter: String = "abdul_basit",
    val audioSpeed: Float = 1.0f,
    val repeatMode: String = "NONE",
    val appLanguage: String = "ENGLISH",
    val enableNotifications: Boolean = true,
    val enableAthan: Boolean = true,
    val calculationMethod: Int = 1
)

