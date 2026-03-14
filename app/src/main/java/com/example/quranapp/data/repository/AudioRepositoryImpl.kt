package com.example.quranapp.data.repository

import com.example.quranapp.domain.model.Reciter
import com.example.quranapp.domain.repository.AudioRepository
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor() : AudioRepository {

    private val recitersStatic: List<Reciter> = listOf(
        Reciter("abdul_basit", "Abdul Basit", "عبد الباسط عبد الصمد", "https://everyayah.com/data/Abdul_Basit_Murattal_192kbps/", "Hafs"),
        Reciter("al_hudhaifi", "Al-Hudhaifi", "صالح الهذيفي", "https://everyayah.com/data/Hudhaifi_128kbps/", "Hafs"),
        Reciter("al_minshawi", "Al-Minshawi", "محمد صديق المنشاوي", "https://everyayah.com/data/Minshawy_Murattal_128kbps/", "Hafs"),
        Reciter("mishary_alfasy", "Mishary Alafasy", "مشاري العفاسي", "https://everyayah.com/data/Alafasy_128kbps/", "Hafs"),
        Reciter("saad_al_ghamdi", "Saad Al-Ghamdi", "سعد الغامدي", "https://everyayah.com/data/Ghamadi_40kbps/", "Hafs")
    )

    override suspend fun getAllReciters(): List<Reciter> {
        return recitersStatic
    }

    override suspend fun getReciterById(id: String): Reciter? {
        return getAllReciters().find { it.id == id }
    }

    override fun getAudioUrl(reciterId: String, surahNumber: Int): String {
        val reciter = recitersStatic.find { it.id == reciterId }
        return "${reciter?.server ?: ""}${String.format("%03d", surahNumber)}.mp3"
    }

    override fun getAyahAudioUrl(reciterId: String, surahNumber: Int, ayahNumber: Int): String {
        val reciter = recitersStatic.find { it.id == reciterId }
        return "${reciter?.server ?: ""}${String.format("%03d", surahNumber)}${String.format("%03d", ayahNumber)}.mp3"
    }

    override suspend fun downloadAudio(reciterId: String, surahNumber: Int): Boolean {
        // Implementation for downloading audio
        // This would involve downloading the file and storing it locally
        return true
    }

    override suspend fun isAudioDownloaded(reciterId: String, surahNumber: Int): Boolean {
        // Check if audio is downloaded locally
        return false
    }

    override suspend fun getDownloadedSurahs(reciterId: String): List<Int> {
        // Return list of downloaded surah numbers
        return emptyList()
    }
}

