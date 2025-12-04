package com.example.quranapp.data.repository

import com.example.quranapp.data.local.dao.AyahDao
import com.example.quranapp.data.local.dao.SurahDao
import com.example.quranapp.data.local.mapper.toDomain
import com.example.quranapp.domain.model.Ayah
import com.example.quranapp.domain.model.Surah
import com.example.quranapp.domain.repository.QuranRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuranRepositoryImpl @Inject constructor(
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao
) : QuranRepository {
    
    override suspend fun getAllSurahs(): List<Surah> {
        return surahDao.getAllSurahsList().map { it.toDomain() }
    }
    
    override suspend fun getSurahByNumber(number: Int): Surah? {
        return surahDao.getSurahByNumber(number)?.toDomain()
    }
    
    override suspend fun getAyahsBySurah(surahNumber: Int): List<Ayah> {
        return ayahDao.getAyahsBySurah(surahNumber).map { it.toDomain() }
    }
    
    override suspend fun getAyah(surahNumber: Int, ayahNumber: Int): Ayah? {
        return ayahDao.getAyah(surahNumber, ayahNumber)?.toDomain()
    }
    
    override suspend fun searchSurahs(query: String): List<Surah> {
        return surahDao.searchSurahs(query).map { it.toDomain() }
    }
    
    override suspend fun searchAyat(query: String): List<Ayah> {
        return ayahDao.searchAyat(query).map { it.toDomain() }
    }
    
    override suspend fun getAyatByPage(page: Int): List<Ayah> {
        return ayahDao.getAyatByPage(page).map { it.toDomain() }
    }
    
    override suspend fun getAyatByJuz(juz: Int): List<Ayah> {
        return ayahDao.getAyatByJuz(juz).map { it.toDomain() }
    }
    
    override fun getSurahsFlow(): Flow<List<Surah>> {
        return surahDao.getAllSurahs().map { surahs -> surahs.map { it.toDomain() } }
    }
}

