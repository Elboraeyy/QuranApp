package com.example.quranapp.di

import android.content.Context
import androidx.room.Room
import com.example.quranapp.data.local.dao.*
import com.example.quranapp.data.local.entity.*
import com.example.quranapp.data.local.database.QuranDatabase
import com.example.quranapp.data.repository.*
import com.example.quranapp.domain.repository.*
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
import com.example.quranapp.domain.repository.TasbihRepository
import com.example.quranapp.data.repository.TasbihRepositoryImpl

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
    fun provideHadithBookmarkDao(database: QuranDatabase): HadithBookmarkDao {
        return database.hadithBookmarkDao()
    }

    @Provides
    fun provideHadithDao(database: QuranDatabase): HadithDao {
        return database.hadithDao()
    }

    @Provides
    fun provideTasbihDao(database: QuranDatabase): TasbihDao {
        return database.tasbihDao()
    }

    @Provides
    fun provideReligiousTaskDao(database: QuranDatabase): ReligiousTaskDao {
        return database.religiousTaskDao()
    }

    @Provides
    fun provideUserStatsDao(database: QuranDatabase): UserStatsDao {
        return database.userStatsDao()
    }

    @Provides
    @Singleton
    fun provideQuranRepository(
        surahDao: SurahDao,
        ayahDao: AyahDao,
        quranApi: QuranApi,
        @ApplicationContext context: Context
    ): QuranRepository {
        return QuranRepositoryImpl(surahDao, ayahDao, quranApi, context)
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
    fun provideAudioPlayerManager(
        @ApplicationContext context: android.content.Context,
        audioRepository: AudioRepository
    ): com.example.quranapp.domain.manager.AudioPlayerManager {
        return com.example.quranapp.data.manager.AudioPlayerManagerImpl(context, audioRepository)
    }
    
    @Provides
    @Singleton
    fun provideTafsirRepository(quranApi: QuranApi): TafsirRepository {
        return TafsirRepositoryImpl(quranApi)
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
        adhkarDao: AdhkarDao
    ): AdhkarRepository {
        return AdhkarRepositoryImpl(adhkarDao)
    }

    @Provides
    @Singleton
    fun provideHadithRepository(
        hadithDao: HadithDao,
        bookmarkDao: HadithBookmarkDao
    ): com.example.quranapp.domain.repository.HadithRepository {
        return com.example.quranapp.data.repository.HadithRepositoryImpl(hadithDao, bookmarkDao)
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
        @ApplicationContext context: Context,
        locationTracker: LocationTracker
    ): QiblaTracker {
        return DefaultQiblaTracker(context, locationTracker)
    }
    
    @Provides
    @Singleton
    fun provideTasbihRepository(
        @ApplicationContext context: Context,
        tasbihDao: TasbihDao
    ): TasbihRepository {
        return TasbihRepositoryImpl(context, tasbihDao)
    }

    @Provides
    @Singleton
    fun provideReligiousTaskRepository(
        religiousTaskDao: ReligiousTaskDao,
        userStatsDao: UserStatsDao
    ): ReligiousTaskRepository {
        return ReligiousTaskRepositoryImpl(religiousTaskDao, userStatsDao)
    }

    @Provides
    @Singleton
    fun provideUserStatsRepository(
        userStatsDao: UserStatsDao
    ): UserStatsRepository {
        return UserStatsRepositoryImpl(userStatsDao)
    }
}
