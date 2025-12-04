package com.example.quranapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quranapp.data.local.entity.SurahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    @Query("SELECT * FROM surahs ORDER BY number")
    fun getAllSurahs(): Flow<List<SurahEntity>>
    
    @Query("SELECT * FROM surahs ORDER BY number")
    suspend fun getAllSurahsList(): List<SurahEntity>
    
    @Query("SELECT * FROM surahs WHERE number = :number")
    suspend fun getSurahByNumber(number: Int): SurahEntity?
    
    @Query("SELECT * FROM surahs WHERE name LIKE '%' || :query || '%' OR nameArabic LIKE '%' || :query || '%' OR nameEnglish LIKE '%' || :query || '%' OR englishNameTranslation LIKE '%' || :query || '%'")
    suspend fun searchSurahs(query: String): List<SurahEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurah(surah: SurahEntity)
}

