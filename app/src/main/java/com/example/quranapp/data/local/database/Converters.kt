package com.example.quranapp.data.local.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        return if (data.isNullOrEmpty()) emptyList() else data.split(",")
    }

    @TypeConverter
    fun fromTaskPeriod(period: com.example.quranapp.data.local.entity.TaskPeriod): String = period.name

    @TypeConverter
    fun toTaskPeriod(value: String): com.example.quranapp.data.local.entity.TaskPeriod =
        com.example.quranapp.data.local.entity.TaskPeriod.valueOf(value)

    @TypeConverter
    fun fromTaskCategory(category: com.example.quranapp.data.local.entity.TaskCategory): String = category.name

    @TypeConverter
    fun toTaskCategory(value: String): com.example.quranapp.data.local.entity.TaskCategory =
        com.example.quranapp.data.local.entity.TaskCategory.valueOf(value)
}

