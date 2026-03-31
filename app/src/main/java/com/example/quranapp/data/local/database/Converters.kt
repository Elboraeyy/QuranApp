package com.example.quranapp.data.local.database

import androidx.room.TypeConverter
import com.example.quranapp.domain.model.HadithVocab
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return gson.toJson(list ?: emptyList<String>())
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data ?: "[]", listType)
    }

    @TypeConverter
    fun fromVocabList(list: List<HadithVocab>?): String {
        return gson.toJson(list ?: emptyList<HadithVocab>())
    }

    @TypeConverter
    fun toVocabList(data: String?): List<HadithVocab> {
        val listType = object : TypeToken<List<HadithVocab>>() {}.type
        return gson.fromJson(data ?: "[]", listType)
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

