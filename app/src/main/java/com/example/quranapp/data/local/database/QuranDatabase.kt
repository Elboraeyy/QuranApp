package com.example.quranapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.quranapp.data.local.dao.AyahDao
import com.example.quranapp.data.local.dao.BookmarkDao
import com.example.quranapp.data.local.dao.FavoriteDao
import com.example.quranapp.data.local.dao.ProgressDao
import com.example.quranapp.data.local.dao.SettingsDao
import com.example.quranapp.data.local.dao.SurahDao
import com.example.quranapp.data.local.entity.AyahEntity
import com.example.quranapp.data.local.entity.BookmarkEntity
import com.example.quranapp.data.local.entity.FavoriteEntity
import com.example.quranapp.data.local.entity.ProgressEntity
import com.example.quranapp.data.local.entity.SettingsEntity
import com.example.quranapp.data.local.entity.SurahEntity

@Database(
    entities = [
        SurahEntity::class,
        AyahEntity::class,
        BookmarkEntity::class,
        ProgressEntity::class,
        FavoriteEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
    abstract fun ayahDao(): AyahDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun progressDao(): ProgressDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun settingsDao(): SettingsDao
}

