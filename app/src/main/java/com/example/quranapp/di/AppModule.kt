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
import com.example.quranapp.data.repository.TranslationRepositoryImpl
import com.example.quranapp.data.repository.AdhkarRepositoryImpl
import com.example.quranapp.domain.repository.AudioRepository
import com.example.quranapp.domain.repository.BookmarkRepository
import com.example.quranapp.domain.repository.FavoriteRepository
import com.example.quranapp.domain.repository.PrayerRepository
import com.example.quranapp.domain.repository.ProgressRepository
import com.example.quranapp.domain.repository.QuranRepository
import com.example.quranapp.domain.repository.SettingsRepository
import com.example.quranapp.domain.repository.TafsirRepository
import com.example.quranapp.domain.repository.TranslationRepository
import com.example.quranapp.domain.repository.AdhkarRepository
import com.example.quranapp.data.local.dao.AdhkarDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.quranapp.data.remote.api.QuranApi
import com.example.quranapp.data.remote.api.PrayerApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.quranapp.domain.location.LocationTracker
import com.example.quranapp.data.location.DefaultLocationTracker
import com.example.quranapp.domain.qibla.QiblaTracker
import com.example.quranapp.data.qibla.DefaultQiblaTracker
import com.example.quranapp.data.repository.AudioRepositoryImpl
import com.example.quranapp.data.repository.BookmarkRepositoryImpl
import com.example.quranapp.data.repository.FavoriteRepositoryImpl
import com.example.quranapp.data.repository.PrayerRepositoryImpl
import com.example.quranapp.data.repository.ProgressRepositoryImpl
import com.example.quranapp.data.repository.QuranRepositoryImpl
import com.example.quranapp.data.repository.SettingsRepositoryImpl
import com.example.quranapp.data.repository.TafsirRepositoryImpl

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
        ).fallbackToDestructiveMigration().build()
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
    fun provideAdhkarDao(database: QuranDatabase): AdhkarDao {
        return database.adhkarDao()
    }
    
    @Provides
    @Singleton
    fun provideQuranRepository(
        surahDao: SurahDao,
        ayahDao: AyahDao,
        quranApi: QuranApi
    ): QuranRepository {
        return QuranRepositoryImpl(surahDao, ayahDao, quranApi)
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
    fun providePrayerRepository(prayerApi: PrayerApi): PrayerRepository {
        return PrayerRepositoryImpl(prayerApi)
    }

    @Provides
    @Singleton
    fun provideAdhkarRepository(
        @ApplicationContext context: Context,
        adhkarDao: AdhkarDao
    ): AdhkarRepository {
        return AdhkarRepositoryImpl(context, adhkarDao)
    }

    @Provides
    @Singleton
    fun provideQuranApi(): QuranApi {
        val logging = okhttp3.logging.HttpLoggingInterceptor().apply {
            level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
        }
        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()
            
        return Retrofit.Builder()
            .baseUrl("https://api.alquran.cloud/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuranApi::class.java)
    }

    @Provides
    @Singleton
    fun providePrayerApi(): PrayerApi {
        return Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PrayerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationTracker(
        fusedLocationProviderClient: FusedLocationProviderClient,
        @ApplicationContext context: Context
    ): LocationTracker {
        return DefaultLocationTracker(
            locationClient = fusedLocationProviderClient,
            context = context
        )
    }

    @Provides
    @Singleton
    fun provideQiblaTracker(
        @ApplicationContext context: Context
    ): QiblaTracker {
        return DefaultQiblaTracker(context)
    }
}

