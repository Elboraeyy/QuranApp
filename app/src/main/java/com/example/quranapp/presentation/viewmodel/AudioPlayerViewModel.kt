package com.example.quranapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.quranapp.domain.model.AyahRef
import com.example.quranapp.domain.model.QcfVerse
import com.example.quranapp.domain.repository.AudioRepository
import com.example.quranapp.domain.repository.SettingsRepository
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.manager.AudioPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val audioPlayerManager: AudioPlayerManager,
    private val settingsRepository: SettingsRepository,
    private val audioRepository: AudioRepository
) : ViewModel() {

    val isPlaying: StateFlow<Boolean> = audioPlayerManager.isPlaying
    val currentPosition: StateFlow<Long> = audioPlayerManager.currentPosition
    val duration: StateFlow<Long> = audioPlayerManager.duration
    val currentSurah: StateFlow<Int> = audioPlayerManager.currentSurah
    val currentAyahIndex: StateFlow<Int> = audioPlayerManager.currentAyahIndex
    val pageCompleted: SharedFlow<Unit> = audioPlayerManager.pageCompleted

    private val _currentPageAyahs = MutableStateFlow<List<AyahRef>>(emptyList())
    val currentPageAyahs: StateFlow<List<AyahRef>> = _currentPageAyahs.asStateFlow()

    private val _isPageMode = MutableStateFlow(false)
    val isPageMode: StateFlow<Boolean> = _isPageMode.asStateFlow()

    private val _selectedReciterName = MutableStateFlow("عبد الباسط عبد الصمد")
    val selectedReciterName: StateFlow<String> = _selectedReciterName.asStateFlow()

    fun playPage(pageVerses: List<QcfVerse>) {
        val ayahs = pageVerses.map { AyahRef(it.surahNumber, it.ayahNumber) }
        _currentPageAyahs.value = ayahs
        _isPageMode.value = true
        viewModelScope.launch {
            val reciterId = settingsRepository.getSettings().selectedReciter
            val reciter = audioRepository.getReciterById(reciterId)
            _selectedReciterName.value = reciter?.nameArabic ?: "عبد الباسط عبد الصمد"
            audioPlayerManager.playPageAyahs(reciterId, ayahs)
        }
    }

    fun playAyah(surahNumber: Int, ayahNumber: Int) {
        _isPageMode.value = false
        viewModelScope.launch {
            val reciterId = settingsRepository.getSettings().selectedReciter
            audioPlayerManager.playAyah(reciterId, surahNumber, ayahNumber)
        }
    }

    fun playSurah(surahNumber: Int) {
        _isPageMode.value = false
        viewModelScope.launch {
            val reciterId = settingsRepository.getSettings().selectedReciter
            audioPlayerManager.playSurah(reciterId, surahNumber)
        }
    }
    
    fun playCurrent() {
        viewModelScope.launch {
            val reciterId = settingsRepository.getSettings().selectedReciter
            audioPlayerManager.playSurah(reciterId, currentSurah.value)
        }
    }

    fun changeReciter(reciterId: String) {
        viewModelScope.launch {
            settingsRepository.updateSelectedReciter(reciterId)
            val reciter = audioRepository.getReciterById(reciterId)
            _selectedReciterName.value = reciter?.nameArabic ?: reciterId
            // If currently playing page, restart with new reciter
            if (_isPageMode.value && _currentPageAyahs.value.isNotEmpty()) {
                audioPlayerManager.playPageAyahs(reciterId, _currentPageAyahs.value)
            }
        }
    }

    fun resume() {
        audioPlayerManager.play()
    }

    fun pause() {
        audioPlayerManager.pause()
    }

    fun stop() {
        audioPlayerManager.pause()
        _isPageMode.value = false
        _currentPageAyahs.value = emptyList()
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
    }
}
