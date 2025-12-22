package com.example.quranapp.data.local.database

import android.content.Context
import com.example.quranapp.data.local.dao.AyahDao
import com.example.quranapp.data.local.dao.SurahDao
import com.example.quranapp.data.local.entity.AyahEntity
import com.example.quranapp.data.local.entity.SurahEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader

class DatabaseInitializer(
    private val context: Context,
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao
) {
    suspend fun initializeDatabase() = withContext(Dispatchers.IO) {
        val existingSurahs = surahDao.getAllSurahsList()
        if (existingSurahs.isNotEmpty()) return@withContext

        val json = readAsset("quran.json")
        if (json != null) {
            val root = JSONArray(json)
            val surahs = mutableListOf<SurahEntity>()
            val ayahs = mutableListOf<AyahEntity>()
            for (i in 0 until root.length()) {
                val sObj = root.getJSONObject(i)
                val number = sObj.getInt("number")
                val surahEntity = SurahEntity(
                    number = number,
                    name = sObj.getString("name"),
                    nameArabic = sObj.getString("nameArabic"),
                    nameEnglish = sObj.getString("nameEnglish"),
                    englishNameTranslation = sObj.getString("englishNameTranslation"),
                    revelationType = sObj.getString("revelationType"),
                    numberOfAyahs = sObj.getInt("numberOfAyahs")
                )
                surahs.add(surahEntity)

                val ayat = sObj.getJSONArray("ayahs")
                for (j in 0 until ayat.length()) {
                    val aObj = ayat.getJSONObject(j)
                    ayahs.add(
                        AyahEntity(
                            number = aObj.getInt("number"),
                            numberInSurah = aObj.getInt("numberInSurah"),
                            surahNumber = number,
                            text = aObj.getString("text"),
                            textUthmani = aObj.getString("textUthmani"),
                            juz = aObj.getInt("juz"),
                            hizb = aObj.getInt("hizb"),
                            manzil = aObj.getInt("manzil"),
                            ruku = aObj.getInt("ruku"),
                            page = aObj.getInt("page"),
                            sajda = aObj.optBoolean("sajda", false)
                        )
                    )
                }
            }
            surahDao.insertSurahs(surahs)
            ayahDao.insertAyat(ayahs)
        }
    }

    private fun readAsset(name: String): String? {
        return try {
            context.assets.open(name).bufferedReader().use(BufferedReader::readText)
        } catch (e: Exception) {
            null
        }
    }
}
