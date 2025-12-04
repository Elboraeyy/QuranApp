package com.example.quranapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quranapp.data.local.entity.AyahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AyahDao {
    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY numberInSurah")
    suspend fun getAyahsBySurah(surahNumber: Int): List<AyahEntity>
    
    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber AND numberInSurah = :ayahNumber")
    suspend fun getAyah(surahNumber: Int, ayahNumber: Int): AyahEntity?
    
    @Query("SELECT * FROM ayahs WHERE page = :page ORDER BY surahNumber, numberInSurah")
    suspend fun getAyatByPage(page: Int): List<AyahEntity>
    
    @Query("SELECT * FROM ayahs WHERE juz = :juz ORDER BY surahNumber, numberInSurah")
    suspend fun getAyatByJuz(juz: Int): List<AyahEntity>
    
    @Query("SELECT * FROM ayahs WHERE text LIKE '%' || :query || '%' OR textUthmani LIKE '%' || :query || '%'")
    suspend fun searchAyat(query: String): List<AyahEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyat(ayahs: List<AyahEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyah(ayah: AyahEntity)
}

