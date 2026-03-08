package com.example.quranapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.quranapp.domain.repository.AudioRepository
import com.example.quranapp.domain.repository.SettingsRepository
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

import androidx.lifecycle.viewModelScope
// Keep existing imports...
import com.example.quranapp.domain.manager.AudioPlayerManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val audioPlayerManager: AudioPlayerManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val isPlaying: StateFlow<Boolean> = audioPlayerManager.isPlaying
    val currentPosition: StateFlow<Long> = audioPlayerManager.currentPosition
    val duration: StateFlow<Long> = audioPlayerManager.duration
    val currentSurah: StateFlow<Int> = audioPlayerManager.currentSurah

    fun playSurah(surahNumber: Int) {
        viewModelScope.launch {
            val reciterId = settingsRepository.getSettings().selectedReciter
            audioPlayerManager.playSurah(reciterId, surahNumber)
        }
    }
    
    // To play the currently selected surah from zero
    fun playCurrent() {
        viewModelScope.launch {
            val reciterId = settingsRepository.getSettings().selectedReciter
            audioPlayerManager.playSurah(reciterId, currentSurah.value)
        }
    }

    fun resume() {
        audioPlayerManager.play()
    }

    fun pause() {
        audioPlayerManager.pause()
    }

    fun seekTo(positionMs: Long) {
        audioPlayerManager.seekTo(positionMs)
    }

    fun next() {
        viewModelScope.launch {
            audioPlayerManager.next()
        }
    }

    fun previous() {
        viewModelScope.launch {
            audioPlayerManager.previous()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // We don't release the player here anymore because the Service will manage its lifecyle.
    }
}
