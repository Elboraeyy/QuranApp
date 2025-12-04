package com.example.quranapp.domain.repository

import com.example.quranapp.domain.model.Ayah
import com.example.quranapp.domain.model.Surah
import kotlinx.coroutines.flow.Flow

interface QuranRepository {
    suspend fun getAllSurahs(): List<Surah>
    suspend fun getSurahByNumber(number: Int): Surah?
    suspend fun getAyahsBySurah(surahNumber: Int): List<Ayah>
    suspend fun getAyah(surahNumber: Int, ayahNumber: Int): Ayah?
    suspend fun searchSurahs(query: String): List<Surah>
    suspend fun searchAyat(query: String): List<Ayah>
    suspend fun getAyatByPage(page: Int): List<Ayah>
    suspend fun getAyatByJuz(juz: Int): List<Ayah>
    fun getSurahsFlow(): Flow<List<Surah>>
}

