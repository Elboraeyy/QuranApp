package com.example.quranapp.data.local.database

import android.content.Context
import com.example.quranapp.data.local.dao.*
import com.example.quranapp.data.local.entity.*
import com.example.quranapp.domain.model.HadithVocab
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseInitializer(
    private val context: Context,
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao,
    private val adhkarDao: AdhkarDao,
    private val hadithDao: HadithDao
) {
    private val gson = Gson()

    suspend fun initializeDatabase() {
        withContext(Dispatchers.IO) {
            try {
                // 1. Initialize Quran Core
                if (surahDao.getAllSurahsList().isEmpty()) {
                    initializeQuranCore()
                }

                // 2. Initialize Adhkar
                if (adhkarDao.getAllCategoriesList().isEmpty()) {
                    initializeAdhkar()
                }

                // 3. Initialize Hadiths
                if (hadithDao.getAllBooks().isEmpty()) {
                    initializeHadiths()
                }
                Unit
            } catch (e: Exception) {
                android.util.Log.e("DatabaseInitializer", "Failed to initialize database", e)
            }
        }
    }

    private suspend fun initializeQuranCore() {
        // Initialize Surahs
        val surahsJson = loadAsset("surahs.json")
        val surahListType = object : TypeToken<List<SurahJson>>() {}.type
        val surahsData: List<SurahJson> = gson.fromJson(surahsJson, surahListType)
        
        val surahEntities = surahsData.map { json ->
            SurahEntity(
                number = json.number,
                name = json.name,
                nameArabic = json.name,
                nameEnglish = json.englishName,
                englishNameTranslation = json.englishNameTranslation,
                revelationType = json.revelationType,
                numberOfAyahs = json.numberOfAyahs
            )
        }
        surahDao.insertSurahs(surahEntities)

        // Initialize Ayahs
        val qcfQuranJson = loadAsset("qcf_quran.json")
        val quranData: QuranJson = gson.fromJson(qcfQuranJson, QuranJson::class.java)
        
        val juzBoundariesJson = loadAsset("juz_boundaries.json")
        val juzBoundaryType = object : TypeToken<List<JuzBoundaryJson>>() {}.type
        val juzBoundaries: List<JuzBoundaryJson> = gson.fromJson(juzBoundariesJson, juzBoundaryType)

        val ayahEntities = quranData.verses.map { json ->
            val parts = json.verse_key.split(":")
            val surahNumber = parts[0].toInt()
            val ayahNumber = parts[1].toInt()
            val juz = calculateJuz(surahNumber, ayahNumber, juzBoundaries)

            AyahEntity(
                number = json.id,
                numberInSurah = ayahNumber,
                surahNumber = surahNumber,
                text = "",
                textUthmani = json.code_v1,
                juz = juz,
                hizb = 0,
                manzil = 0,
                ruku = 0,
                page = json.v1_page,
                sajda = false
            )
        }
        
        ayahEntities.chunked(500).forEach { chunk ->
            ayahDao.insertAyat(chunk)
        }
    }

    private suspend fun initializeAdhkar() {
        val adhkarJson = loadAsset("adhkar.json")
        val adhkarData = gson.fromJson(adhkarJson, AdhkarRootJson::class.java)
        
        val categories = mutableListOf<AdhkarCategoryEntity>()
        val items = mutableListOf<AdhkarItemEntity>()
        
        adhkarData.categories.forEach { catJson ->
            categories.add(
                AdhkarCategoryEntity(
                    id = catJson.id,
                    title = catJson.title,
                    description = catJson.description ?: ""
                )
            )
            catJson.items.forEach { itemJson ->
                items.add(
                    AdhkarItemEntity(
                        id = itemJson.id,
                        categoryId = catJson.id,
                        text = itemJson.text,
                        targetCount = itemJson.count,
                        reference = itemJson.reference ?: ""
                    )
                )
            }
        }
        
        adhkarDao.insertCategories(categories)
        adhkarDao.insertItems(items)
    }

    private suspend fun initializeHadiths() {
        val hadithsJson = loadAsset("hadiths.json")
        val hadithData = gson.fromJson(hadithsJson, HadithRootJson::class.java)
        
        hadithDao.insertBooks(hadithData.books.map {
            HadithBookEntity(it.id, it.title, it.author ?: "", it.description ?: "")
        })
        
        hadithDao.insertCategories(hadithData.categories.map {
            HadithCategoryEntity(it.id, it.title)
        })
        
        val hadithEntities = hadithData.hadiths.map {
            HadithEntity(
                id = it.id,
                bookId = it.bookId,
                categoryId = it.categoryId,
                narrator = it.narrator ?: "",
                text = it.text,
                source = it.source ?: "",
                explanation = it.explanation ?: "",
                vocabulary = it.vocabulary ?: emptyList(),
                lifeApplications = it.lifeApplications ?: emptyList()
            )
        }
        
        hadithEntities.chunked(200).forEach { chunk ->
            hadithDao.insertHadiths(chunk)
        }
    }

    private fun loadAsset(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    private fun calculateJuz(surah: Int, ayah: Int, boundaries: List<JuzBoundaryJson>): Int {
        for (i in boundaries.indices.reversed()) {
            val boundary = boundaries[i]
            if (surah > boundary.surah || (surah == boundary.surah && ayah >= boundary.ayah)) {
                return boundary.juz
            }
        }
        return 1
    }

    // JSON Mapping Classes
    private data class SurahJson(
        val number: Int,
        val name: String,
        val englishName: String,
        val englishNameTranslation: String,
        val revelationType: String,
        val numberOfAyahs: Int
    )

    private data class AyahJson(
        val id: Int,
        val verse_key: String,
        val code_v1: String,
        val v1_page: Int
    )

    private data class QuranJson(
        val verses: List<AyahJson>
    )

    private data class JuzBoundaryJson(
        val juz: Int,
        val surah: Int,
        val ayah: Int
    )

    private data class AdhkarRootJson(val categories: List<AdhkarCategoryJson>)
    private data class AdhkarCategoryJson(
        val id: Int,
        val title: String,
        val description: String?,
        val items: List<AdhkarItemJson>
    )
    private data class AdhkarItemJson(
        val id: Int,
        val text: String,
        val count: Int,
        val reference: String?
    )

    private data class HadithRootJson(
        val books: List<HadithBookJson>,
        val categories: List<HadithCategoryJson>,
        val hadiths: List<HadithItemJson>
    )
    private data class HadithBookJson(val id: Int, val title: String, val author: String?, val description: String?)
    private data class HadithCategoryJson(val id: Int, val title: String)
    private data class HadithItemJson(
        val id: Int,
        val bookId: Int,
        val categoryId: Int,
        val narrator: String?,
        val text: String,
        val source: String?,
        val explanation: String?,
        val vocabulary: List<com.example.quranapp.domain.model.HadithVocab>?,
        val lifeApplications: List<String>?
    )
}

