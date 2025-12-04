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

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioRepository: AudioRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

    private var currentSurah = 1

    suspend fun playCurrent() {
        val reciterId = settingsRepository.getSettings().selectedReciter
        val url = audioRepository.getAudioUrl(reciterId, currentSurah)
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun pause() {
        player.pause()
    }

    suspend fun next() {
        currentSurah = (currentSurah % 114) + 1
        playCurrent()
    }

    suspend fun previous() {
        currentSurah = if (currentSurah == 1) 114 else currentSurah - 1
        playCurrent()
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
