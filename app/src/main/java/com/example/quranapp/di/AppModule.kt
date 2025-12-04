package com.example.quranapp.di

import android.content.Context
import androidx.room.Room
import com.example.quranapp.data.local.dao.AyahDao
import com.example.quranapp.data.local.dao.BookmarkDao
import com.example.quranapp.data.local.dao.FavoriteDao
import com.example.quranapp.data.local.dao.ProgressDao
import com.example.quranapp.data.local.dao.SettingsDao
import com.example.quranapp.data.local.dao.SurahDao
import com.example.quranapp.data.local.database.QuranDatabase
import com.example.quranapp.data.repository.AudioRepositoryImpl
import com.example.quranapp.data.repository.BookmarkRepositoryImpl
import com.example.quranapp.data.repository.FavoriteRepositoryImpl
import com.example.quranapp.data.repository.PrayerRepositoryImpl
import com.example.quranapp.data.repository.ProgressRepositoryImpl
import com.example.quranapp.data.repository.QuranRepositoryImpl
import com.example.quranapp.data.repository.SettingsRepositoryImpl
import com.example.quranapp.data.repository.TafsirRepositoryImpl
import com.example.quranapp.data.repository.TranslationRepositoryImpl
import com.example.quranapp.domain.repository.AudioRepository
import com.example.quranapp.domain.repository.BookmarkRepository
import com.example.quranapp.domain.repository.FavoriteRepository
import com.example.quranapp.domain.repository.PrayerRepository
import com.example.quranapp.domain.repository.ProgressRepository
import com.example.quranapp.domain.repository.QuranRepository
import com.example.quranapp.domain.repository.SettingsRepository
import com.example.quranapp.domain.repository.TafsirRepository
import com.example.quranapp.domain.repository.TranslationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideQuranDatabase(@ApplicationContext context: Context): QuranDatabase {
        return Room.databaseBuilder(
            context,
            QuranDatabase::class.java,
            "quran_database"
        ).build()
    }
    
    @Provides
    fun provideSurahDao(database: QuranDatabase): SurahDao {
        return database.surahDao()
    }
    
    @Provides
    fun provideAyahDao(database: QuranDatabase): AyahDao {
        return database.ayahDao()
    }
    
    @Provides
    fun provideBookmarkDao(database: QuranDatabase): BookmarkDao {
        return database.bookmarkDao()
    }
    
    @Provides
    fun provideProgressDao(database: QuranDatabase): ProgressDao {
        return database.progressDao()
    }
    
    @Provides
    fun provideFavoriteDao(database: QuranDatabase): FavoriteDao {
        return database.favoriteDao()
    }
    
    @Provides
    fun provideSettingsDao(database: QuranDatabase): SettingsDao {
        return database.settingsDao()
    }
    
    @Provides
    @Singleton
    fun provideQuranRepository(
        surahDao: SurahDao,
        ayahDao: AyahDao
    ): QuranRepository {
        return QuranRepositoryImpl(surahDao, ayahDao)
    }
    
    @Provides
    @Singleton
    fun provideBookmarkRepository(
        bookmarkDao: BookmarkDao
    ): BookmarkRepository {
        return BookmarkRepositoryImpl(bookmarkDao)
    }
    
    @Provides
    @Singleton
    fun provideProgressRepository(
        progressDao: ProgressDao
    ): ProgressRepository {
        return ProgressRepositoryImpl(progressDao)
    }
    
    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsDao: SettingsDao
    ): SettingsRepository {
        return SettingsRepositoryImpl(settingsDao)
    }
    
    @Provides
    @Singleton
    fun provideFavoriteRepository(
        favoriteDao: FavoriteDao
    ): FavoriteRepository {
        return FavoriteRepositoryImpl(favoriteDao)
    }
    
    @Provides
    @Singleton
    fun provideAudioRepository(): AudioRepository {
        return AudioRepositoryImpl()
    }
    
    @Provides
    @Singleton
    fun provideTafsirRepository(): TafsirRepository {
        return TafsirRepositoryImpl()
    }
    
    @Provides
    @Singleton
    fun provideTranslationRepository(): TranslationRepository {
        return TranslationRepositoryImpl()
    }
    
    @Provides
    @Singleton
    fun providePrayerRepository(): PrayerRepository {
        return PrayerRepositoryImpl()
    }
}

