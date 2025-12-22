package com.example.quranapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.quranapp.domain.repository.AudioRepository
import com.example.quranapp.domain.repository.QuranRepository
import com.example.quranapp.domain.repository.SettingsRepository
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioRepository: AudioRepository,
    private val settingsRepository: SettingsRepository,
    private val quranRepository: QuranRepository
) : ViewModel() {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val httpClient = OkHttpClient()

    private var currentSurah = 1

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    private val _reciterName = MutableStateFlow("")
    val reciterName: StateFlow<String> = _reciterName.asStateFlow()

    private val _surahName = MutableStateFlow("")
    val surahName: StateFlow<String> = _surahName.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _durationMs.value = player.duration.coerceAtLeast(0L)
            }

            override fun onPlayerError(error: com.google.android.exoplayer2.PlaybackException) {
                _errorMessage.value = error.message
                _isPlaying.value = false
            }
        })
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.Main) {
            while (true) {
                _positionMs.value = player.currentPosition
                kotlinx.coroutines.delay(500)
            }
        }
    }

    suspend fun playCurrent() {
        val reciterId = settingsRepository.getSettings().selectedReciter
        _reciterName.value = audioRepository.getAllReciters().find { it.id == reciterId }?.name ?: ""
        _surahName.value = quranRepository.getSurahByNumber(currentSurah)?.nameEnglish ?: ""
        val url = resolveUrl(reciterId, currentSurah)
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

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _positionMs.value = positionMs
    }

    private suspend fun resolveUrl(reciterId: String, surahNumber: Int): String {
        val base = audioRepository.getAudioUrl(reciterId, surahNumber)
        val candidates = mutableListOf(base)
        val replaced8 = base.replace("server7", "server8")
        val replaced11 = base.replace("server7", "server11")
        if (replaced8 != base) candidates.add(replaced8)
        if (replaced11 != base) candidates.add(replaced11)
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            for (u in candidates) {
                try {
                    val req = Request.Builder().url(u).method("HEAD", null).build()
                    httpClient.newCall(req).execute().use { resp ->
                        if (resp.isSuccessful) return@withContext u
                    }
                } catch (_: Exception) { }
            }
            base
        }
    }
}
