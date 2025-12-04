package com.example.quranapp.data.local.database

import com.example.quranapp.data.local.dao.AyahDao
import com.example.quranapp.data.local.dao.SurahDao
import com.example.quranapp.data.local.entity.AyahEntity
import com.example.quranapp.data.local.entity.SurahEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseInitializer(
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao
) {
    suspend fun initializeDatabase() = withContext(Dispatchers.IO) {
        // Check if database is already initialized
        val existingSurahs = surahDao.getAllSurahsList()
        if (existingSurahs.isNotEmpty()) {
            return@withContext
        }

        // Initialize with basic data
        // In a real app, you would load this from a JSON file or API
        val surahs = getInitialSurahs()
        surahDao.insertSurahs(surahs)

        // Initialize with sample ayahs for Al-Fatiha
        val ayahs = getInitialAyahs()
        ayahDao.insertAyat(ayahs)
    }

    private fun getInitialSurahs(): List<SurahEntity> {
        return listOf(
            SurahEntity(
                number = 1,
                name = "Al-Fatiha",
                nameArabic = "الفاتحة",
                nameEnglish = "Al-Fatiha",
                englishNameTranslation = "The Opening",
                revelationType = "Meccan",
                numberOfAyahs = 7
            ),
            SurahEntity(
                number = 2,
                name = "Al-Baqarah",
                nameArabic = "البقرة",
                nameEnglish = "Al-Baqarah",
                englishNameTranslation = "The Cow",
                revelationType = "Medinan",
                numberOfAyahs = 286
            )
            // Add more surahs as needed
        )
    }

    private fun getInitialAyahs(): List<AyahEntity> {
        return listOf(
            AyahEntity(
                number = 1,
                numberInSurah = 1,
                surahNumber = 1,
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                textUthmani = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                juz = 1,
                hizb = 1,
                manzil = 1,
                ruku = 1,
                page = 1,
                sajda = false
            )
            // Add more ayahs as needed
        )
    }
}

