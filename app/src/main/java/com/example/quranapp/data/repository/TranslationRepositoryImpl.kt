package com.example.quranapp.data.repository

import android.content.Context
import com.example.quranapp.domain.model.Translation
import com.example.quranapp.domain.model.TranslationText
import com.example.quranapp.domain.repository.TranslationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import javax.inject.Inject

class TranslationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TranslationRepository {
    
    override suspend fun getAllTranslations(): List<Translation> {
        return listOf(
            Translation("en_sahih", "Sahih International", "en", "en"),
            Translation("ur_maududi", "Urdu - Maududi", "ur", "ur"),
            Translation("fr_hameedullah", "French - Hamidullah", "fr", "fr"),
            Translation("ms_basmeih", "Malay - Basmeih", "ms", "ms"),
            Translation("id_jalalayn", "Indonesian - Jalalayn", "id", "id")
        )
    }
    
    override suspend fun getTranslationById(id: String): Translation? {
        return getAllTranslations().find { it.id == id }
    }
    
    override suspend fun getTranslationText(surahNumber: Int, ayahNumber: Int, translationId: String): TranslationText? {
        val list = getTranslationTextsBySurah(surahNumber, translationId)
        return list.find { it.ayahNumber == ayahNumber }
    }
    
    override suspend fun getTranslationTextsBySurah(surahNumber: Int, translationId: String): List<TranslationText> {
        val name = "translations_${translationId}.json"
        val json = try { context.assets.open(name).bufferedReader().readText() } catch (e: Exception) { null }
        if (json == null) return emptyList()
        val arr = JSONArray(json)
        val result = mutableListOf<TranslationText>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            if (obj.getInt("surahNumber") == surahNumber) {
                result.add(
                    TranslationText(
                        surahNumber = surahNumber,
                        ayahNumber = obj.getInt("ayahNumber"),
                        translationId = translationId,
                        text = obj.getString("text")
                    )
                )
            }
        }
        return result
    }
    
    override suspend fun cacheTranslation(translationText: TranslationText) {
        // Cache translation text locally
    }
}

