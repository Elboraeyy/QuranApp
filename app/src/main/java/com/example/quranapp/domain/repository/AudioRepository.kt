package com.example.quranapp.domain.repository

import com.example.quranapp.domain.model.Reciter
import kotlinx.coroutines.flow.Flow

interface AudioRepository {
    suspend fun getAllReciters(): List<Reciter>
    suspend fun getReciterById(id: String): Reciter?
    fun getAudioUrl(reciterId: String, surahNumber: Int): String
    fun getAyahAudioUrl(reciterId: String, surahNumber: Int, ayahNumber: Int): String
    suspend fun downloadAudio(reciterId: String, surahNumber: Int): Boolean
    suspend fun isAudioDownloaded(reciterId: String, surahNumber: Int): Boolean
    suspend fun getDownloadedSurahs(reciterId: String): List<Int>
}

