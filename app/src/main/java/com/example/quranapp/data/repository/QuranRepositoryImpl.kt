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

import com.example.quranapp.data.remote.api.QuranApi
import com.example.quranapp.data.local.entity.SurahEntity
import com.example.quranapp.data.local.entity.AyahEntity
import com.example.quranapp.data.remote.dto.FullQuranResponse
import com.example.quranapp.data.remote.dto.FullQuranSurahDto
import com.example.quranapp.data.remote.dto.AyahDto

class QuranRepositoryImpl @Inject constructor(
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao,
    private val quranApi: QuranApi
) : QuranRepository {
    
    override suspend fun getAllSurahs(): List<Surah> {
        val localSurahs = surahDao.getAllSurahsList()
        if (localSurahs.isEmpty()) {
            try {
                // Because 'getFullQuran' API returns a massive JSON payload that causes OutOfMemoryError, 
                // we first fetch just the meta information for all Surahs to populate the Index.
                val metaResponse = quranApi.getQuranMeta() // getQuranMeta() is the correct metadata endpoint
                val metaSurahs = metaResponse.data.surahs.references
                
                // Convert metadata to entities and save them to DB for the index
                val surahEntities = metaSurahs.map { dto ->
                    com.example.quranapp.data.local.entity.SurahEntity(
                        number = dto.number,
                        name = dto.name,
                        nameArabic = dto.name,
                        nameEnglish = dto.englishName,
                        englishNameTranslation = dto.englishNameTranslation,
                        revelationType = dto.revelationType,
                        numberOfAyahs = dto.numberOfAyahs
                    )
                }
                surahDao.insertSurahs(surahEntities)
            } catch (e: Exception) {
                android.util.Log.e("QuranRepositoryImpl", "Failed to fetch Surahs meta", e)
                e.printStackTrace()
            }
        }
        return surahDao.getAllSurahsList().map { it.toDomain() }
    }
    
    override suspend fun getSurahByNumber(number: Int): Surah? {
        return surahDao.getSurahByNumber(number)?.toDomain()
    }
    
    override suspend fun getAyahsBySurah(surahNumber: Int): List<Ayah> {
        // First check locally
        val localAyahs = ayahDao.getAyahsBySurah(surahNumber)
        
        if (localAyahs.isEmpty()) {
            try {
                // Fetch individually if not present
                val response = quranApi.getSurahDetail(surahNumber)
                val surahDto = response.data ?: return emptyList()
                
                val entities = surahDto.ayahs.map { ayahDto ->
                    com.example.quranapp.data.local.entity.AyahEntity(
                        number = ayahDto.number,
                        numberInSurah = ayahDto.numberInSurah,
                        surahNumber = surahNumber,
                        text = ayahDto.text,
                        textUthmani = ayahDto.text, // Assuming text is Uthmani if endpoint provides it
                        juz = ayahDto.juz,
                        hizb = ayahDto.hizbQuarter,
                        manzil = ayahDto.manzil,
                        ruku = ayahDto.ruku,
                        page = ayahDto.page,
                        sajda = false
                    )
                }
                ayahDao.insertAyat(entities)
            } catch (e: Exception) {
                android.util.Log.e("QuranRepositoryImpl", "Failed to fetch Surah detail", e)
            }
        }
        
        // Return from DB
        val updatedLocal = ayahDao.getAyahsBySurah(surahNumber)
        return updatedLocal.map { it.toDomain() }
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
    
    override suspend fun getJuzBoundaries(): List<Ayah> {
        return try {
            val metaResponse = quranApi.getQuranMeta()
            val juzRefs = metaResponse.data.juzs.references
            juzRefs.mapIndexed { index, dto ->
                Ayah(
                    number = 0,
                    numberInSurah = dto.ayah,
                    surahNumber = dto.surah,
                    text = "بداية الجزء ${index + 1}",
                    textUthmani = "",
                    juz = index + 1,
                    hizb = 0,
                    manzil = 0,
                    ruku = 0,
                    page = 0,
                    sajda = false
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("QuranRepositoryImpl", "Failed to get juz boundaries from API", e)
            ayahDao.getJuzBoundaries().map { it.toDomain() }
        }
    }
    
    override fun getSurahsFlow(): Flow<List<Surah>> {
        return surahDao.getAllSurahs().map { surahs -> surahs.map { it.toDomain() } }
    }
}

