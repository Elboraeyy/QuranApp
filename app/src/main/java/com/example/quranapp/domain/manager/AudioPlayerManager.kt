package com.example.quranapp.domain.manager

import com.example.quranapp.domain.model.AyahRef
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow

interface AudioPlayerManager {
    val player: Player
    val isPlaying: StateFlow<Boolean>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>
    val currentSurah: StateFlow<Int>
    val currentAyahIndex: StateFlow<Int>
    val pageCompleted: SharedFlow<Unit>
    
    suspend fun playSurah(reciterId: String, surahNumber: Int)
    suspend fun playPageAyahs(reciterId: String, ayahs: List<AyahRef>)
    suspend fun playAyah(reciterId: String, surahNumber: Int, ayahNumber: Int)
    fun pause()
    fun play()
    fun seekTo(positionMs: Long)
    suspend fun next()
    suspend fun previous()
    fun release()
}
