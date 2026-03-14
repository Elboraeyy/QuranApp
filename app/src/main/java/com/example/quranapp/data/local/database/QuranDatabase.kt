package com.example.quranapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.quranapp.data.local.dao.AdhkarDao
import com.example.quranapp.data.local.dao.AyahDao
import com.example.quranapp.data.local.dao.BookmarkDao
import com.example.quranapp.data.local.dao.FavoriteDao
import com.example.quranapp.data.local.dao.ProgressDao
import com.example.quranapp.data.local.dao.SettingsDao
import com.example.quranapp.data.local.dao.SurahDao
import com.example.quranapp.data.local.dao.TasbihDao
import com.example.quranapp.data.local.dao.HadithBookmarkDao
import com.example.quranapp.data.local.dao.ReligiousTaskDao
import com.example.quranapp.data.local.dao.UserStatsDao
import com.example.quranapp.data.local.entity.HadithBookmarkEntity
import com.example.quranapp.data.local.entity.ReligiousTaskEntity
import com.example.quranapp.data.local.entity.TaskCompletionEntity
import com.example.quranapp.data.local.entity.UserStatsEntity
import com.example.quranapp.data.local.entity.AdhkarProgressEntity
import com.example.quranapp.data.local.entity.AyahEntity
import com.example.quranapp.data.local.entity.BookmarkEntity
import com.example.quranapp.data.local.entity.FavoriteEntity
import com.example.quranapp.data.local.entity.ProgressEntity
import com.example.quranapp.data.local.entity.SettingsEntity
import com.example.quranapp.data.local.entity.SurahEntity
import com.example.quranapp.data.local.entity.TasbihEntity

@Database(
    entities = [
        SurahEntity::class,
        AyahEntity::class,
        BookmarkEntity::class,
        ProgressEntity::class,
        FavoriteEntity::class,
        SettingsEntity::class,
        AdhkarProgressEntity::class,
        TasbihEntity::class,
        HadithBookmarkEntity::class,
        ReligiousTaskEntity::class,
        TaskCompletionEntity::class,
        UserStatsEntity::class
    ],
    version = 8,
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
    abstract fun religiousTaskDao(): ReligiousTaskDao
    abstract fun userStatsDao(): UserStatsDao
}

