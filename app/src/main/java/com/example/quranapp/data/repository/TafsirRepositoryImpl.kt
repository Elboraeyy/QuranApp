package com.example.quranapp.data.repository

import com.example.quranapp.domain.model.Tafsir
import com.example.quranapp.domain.model.TafsirText
import com.example.quranapp.domain.repository.TafsirRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

import com.example.quranapp.data.remote.api.QuranApi

class TafsirRepositoryImpl @Inject constructor(
    private val quranApi: QuranApi
) : TafsirRepository {
    
    override suspend fun getAllTafsirs(): List<Tafsir> {
        return listOf(
            Tafsir("ar.jalalayn", "تفسير الجلالين", "تفسير الجلالين المبسط", "ar"),
            Tafsir("ar.muyassar", "تفسير الميسر", "التفسير الميسر للآيات", "ar")
        )
    }
    
    override suspend fun getTafsirById(id: String): Tafsir? {
        return getAllTafsirs().find { it.id == id }
    }
    
    override suspend fun getTafsirText(surahNumber: Int, ayahNumber: Int, tafsirId: String): TafsirText? {
        return getTafsirTextsBySurah(surahNumber, tafsirId).find { it.ayahNumber == ayahNumber }
    }
    
    override suspend fun getTafsirTextsBySurah(surahNumber: Int, tafsirId: String): List<TafsirText> {
        val result = quranApi.getSurahTafsir(surahNumber, tafsirId)
        return result.data.ayahs.map { ayahDto ->
            TafsirText(
                surahNumber = surahNumber,
                ayahNumber = ayahDto.numberInSurah,
                tafsirId = tafsirId,
                text = ayahDto.text
            )
        }
    }
    
    override suspend fun cacheTafsir(tafsirText: TafsirText) {
        // Cache tafsir text locally
    }
}

