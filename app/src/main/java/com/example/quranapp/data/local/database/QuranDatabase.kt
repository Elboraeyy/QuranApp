package com.example.quranapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.quranapp.data.local.dao.*
import com.example.quranapp.data.local.entity.*

@Database(
    entities = [
        SurahEntity::class,
        AyahEntity::class,
        BookmarkEntity::class,
        ProgressEntity::class,
        FavoriteEntity::class,
        SettingsEntity::class,
        AdhkarProgressEntity::class,
        AdhkarCategoryEntity::class,
        AdhkarItemEntity::class,
        TasbihEntity::class,
        HadithBookmarkEntity::class,
        HadithBookEntity::class,
        HadithCategoryEntity::class,
        HadithEntity::class,
        ReligiousTaskEntity::class,
        TaskCompletionEntity::class,
        UserStatsEntity::class
    ],
    version = 9,
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
    abstract fun adhkarDao(): AdhkarDao
    abstract fun tasbihDao(): TasbihDao
    abstract fun hadithBookmarkDao(): HadithBookmarkDao
    abstract fun hadithDao(): HadithDao
    abstract fun religiousTaskDao(): ReligiousTaskDao
    abstract fun userStatsDao(): UserStatsDao
}

