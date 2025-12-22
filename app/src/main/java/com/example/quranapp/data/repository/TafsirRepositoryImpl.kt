package com.example.quranapp.data.repository

import android.content.Context
import com.example.quranapp.domain.model.Tafsir
import com.example.quranapp.domain.model.TafsirText
import com.example.quranapp.domain.repository.TafsirRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import javax.inject.Inject

class TafsirRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TafsirRepository {
    
    override suspend fun getAllTafsirs(): List<Tafsir> {
        return listOf(
            Tafsir("jalalayn", "Tafsir Al-Jalalayn", "تفسير الجلالين", "ar"),
            Tafsir("ibn_kathir", "Tafsir Ibn Kathir", "تفسير ابن كثير", "ar"),
            Tafsir("tabari", "Tafsir Al-Tabari", "تفسير الطبري", "ar")
        )
    }
    
    override suspend fun getTafsirById(id: String): Tafsir? {
        return getAllTafsirs().find { it.id == id }
    }
    
    override suspend fun getTafsirText(surahNumber: Int, ayahNumber: Int, tafsirId: String): TafsirText? {
        val list = getTafsirTextsBySurah(surahNumber, tafsirId)
        return list.find { it.ayahNumber == ayahNumber }
    }
    
    override suspend fun getTafsirTextsBySurah(surahNumber: Int, tafsirId: String): List<TafsirText> {
        val name = "tafsir_${tafsirId}.json"
        val json = try { context.assets.open(name).bufferedReader().readText() } catch (e: Exception) { null }
        if (json == null) return emptyList()
        val arr = JSONArray(json)
        val result = mutableListOf<TafsirText>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            if (obj.getInt("surahNumber") == surahNumber) {
                result.add(
                    TafsirText(
                        surahNumber = surahNumber,
                        ayahNumber = obj.getInt("ayahNumber"),
                        tafsirId = tafsirId,
                        text = obj.getString("text")
                    )
                )
            }
        }
        return result
    }
    
    override suspend fun cacheTafsir(tafsirText: TafsirText) {
        // Cache tafsir text locally
    }
}

