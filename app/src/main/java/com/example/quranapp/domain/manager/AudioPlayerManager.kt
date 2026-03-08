package com.example.quranapp.domain.manager

import com.google.android.exoplayer2.Player
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerManager {
    val player: Player
    val isPlaying: StateFlow<Boolean>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>
    val currentSurah: StateFlow<Int>
    
    suspend fun playSurah(reciterId: String, surahNumber: Int)
    fun pause()
    fun play()
    fun seekTo(positionMs: Long)
    suspend fun next()
    suspend fun previous()
    fun release()
}
