package com.example.quranapp.data.repository

import android.content.Context
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
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray

class QuranRepositoryImpl @Inject constructor(
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao,
    private val quranApi: QuranApi,
    @ApplicationContext private val context: Context
) : QuranRepository {

    // In-memory cache
    private var cachedSurahs: List<Surah>? = null
    private var cachedJuzBoundaries: List<Ayah>? = null
    
    // QCF V1 Cache
    private var cachedQcfVerses: List<com.example.quranapp.domain.model.QcfVerse>? = null

    
    override suspend fun getAllSurahs(): List<Surah> {
        // Return from memory cache first
        cachedSurahs?.let { return it }

        // Load from local assets file — instant, no network
        val result = try {
            val jsonString = context.assets.open("surahs.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val surahs = mutableListOf<Surah>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                surahs.add(
                    Surah(
                        number = obj.getInt("number"),
                        name = obj.getString("name"),
                        nameArabic = obj.getString("name"),
                        nameEnglish = obj.getString("englishName"),
                        englishNameTranslation = obj.getString("englishNameTranslation"),
                        revelationType = obj.getString("revelationType"),
                        numberOfAyahs = obj.getInt("numberOfAyahs")
                    )
                )
            }
            surahs
        } catch (e: Exception) {
            android.util.Log.e("QuranRepositoryImpl", "Failed to load surahs from assets", e)
            emptyList()
        }
        cachedSurahs = result
        return result
    }
    
    override suspend fun getSurahByNumber(number: Int): Surah? {
        val allSurahs = getAllSurahs()
        return allSurahs.find { it.number == number }
    }
    
    override suspend fun getAyahsBySurah(surahNumber: Int): List<Ayah> {
        val localAyahs = ayahDao.getAyahsBySurah(surahNumber)
        
        if (localAyahs.isEmpty()) {
            try {
                val response = quranApi.getSurahDetail(surahNumber)
                val surahDto = response.data ?: return emptyList()
                
                val entities = surahDto.ayahs.map { ayahDto ->
                    AyahEntity(
                        number = ayahDto.number,
                        numberInSurah = ayahDto.numberInSurah,
                        surahNumber = surahNumber,
                        text = ayahDto.text,
                        textUthmani = ayahDto.text,
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
        
        val updatedLocal = ayahDao.getAyahsBySurah(surahNumber)
        return updatedLocal.map { it.toDomain() }
    }
    
    override suspend fun getAyah(surahNumber: Int, ayahNumber: Int): Ayah? {
        return ayahDao.getAyah(surahNumber, ayahNumber)?.toDomain()
    }
    
    override suspend fun searchSurahs(query: String): List<Surah> {
        val allSurahs = getAllSurahs()
        return allSurahs.filter { 
            it.nameArabic.contains(query) || it.nameEnglish.contains(query, ignoreCase = true)
        }
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
        // Return from memory cache first
        cachedJuzBoundaries?.let { return it }

        // Load from local assets file — instant, no network
        val allSurahs = getAllSurahs()
        val result = try {
            val jsonString = context.assets.open("juz_boundaries.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val boundaries = mutableListOf<Ayah>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val juzNumber = obj.getInt("juz")
                val surahNumber = obj.getInt("surah")
                val ayahNumber = obj.getInt("ayah")
                val surah = allSurahs.find { it.number == surahNumber }
                boundaries.add(
                    Ayah(
                        number = 0,
                        numberInSurah = ayahNumber,
                        surahNumber = surahNumber,
                        text = "بداية الجزء $juzNumber",
                        textUthmani = "",
                        juz = juzNumber,
                        hizb = 0,
                        manzil = 0,
                        ruku = 0,
                        page = 0,
                        sajda = false
                    )
                )
            }
            boundaries
        } catch (e: Exception) {
            android.util.Log.e("QuranRepositoryImpl", "Failed to load juz boundaries from assets", e)
            emptyList()
        }
        cachedJuzBoundaries = result
        return result
    }
    
    override suspend fun getQcfPage(pageNumber: Int): com.example.quranapp.domain.model.QcfPage {
        val linesMap = getQcfLinesForPage(pageNumber)
        val lines = linesMap.entries
            .sortedBy { it.key.toIntOrNull() ?: 0 }
            .map { (lineNum, text) ->
                com.example.quranapp.domain.model.QcfLine(
                    lineNumber = lineNum.toIntOrNull() ?: 0,
                    text = text
                )
            }
        
        // Also get verse info for this page to determine surah headers
        val allVerses = getQcfVerses()
        val pageVerses = allVerses.filter { it.page == pageNumber }
        
        return com.example.quranapp.domain.model.QcfPage(
            pageNumber = pageNumber,
            lines = lines,
            verses = pageVerses
        )
    }
    
    // Cache for qcf_lines.json
    private var cachedQcfLines: org.json.JSONObject? = null
    
    private suspend fun getQcfLinesForPage(pageNumber: Int): Map<String, String> {
        if (cachedQcfLines == null) {
            try {
                val jsonString = context.assets.open("qcf_lines.json").bufferedReader().use { it.readText() }
                cachedQcfLines = org.json.JSONObject(jsonString)
            } catch (e: Exception) {
                android.util.Log.e("QuranRepositoryImpl", "Failed to load qcf_lines.json", e)
                return emptyMap()
            }
        }
        
        val pageObj = cachedQcfLines?.optJSONObject(pageNumber.toString()) ?: return emptyMap()
        val result = mutableMapOf<String, String>()
        val keys = pageObj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            result[key] = pageObj.getString(key)
        }
        return result
    }
    
    private suspend fun getQcfVerses(): List<com.example.quranapp.domain.model.QcfVerse> {
        cachedQcfVerses?.let { return it }
        val result = try {
            val jsonString = context.assets.open("qcf_quran.json").bufferedReader().use { it.readText() }
            val jsonObject = org.json.JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray("verses")
            val verses = mutableListOf<com.example.quranapp.domain.model.QcfVerse>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val verseKey = obj.getString("verse_key").split(":")
                verses.add(
                    com.example.quranapp.domain.model.QcfVerse(
                        id = obj.getInt("id"),
                        surahNumber = verseKey[0].toInt(),
                        ayahNumber = verseKey[1].toInt(),
                        codeV1 = obj.getString("code_v1"),
                        page = obj.getInt("v1_page")
                    )
                )
            }
            verses
        } catch (e: Exception) {
            android.util.Log.e("QuranRepositoryImpl", "Failed to load QCF mapped data", e)
            emptyList()
        }
        cachedQcfVerses = result
        return result
    }
    
    override fun getSurahsFlow(): Flow<List<Surah>> {
        return surahDao.getAllSurahs().map { surahs -> surahs.map { it.toDomain() } }
    }
    
    override suspend fun getSurahStartPages(): Map<Int, Int> {
        val verses = getQcfVerses()
        val pageMap = mutableMapOf<Int, Int>()
        for (verse in verses) {
            if (verse.ayahNumber == 1 && !pageMap.containsKey(verse.surahNumber)) {
                pageMap[verse.surahNumber] = verse.page
            }
        }
        return pageMap
    }
}
