package com.example.quranapp.data.manager

import android.content.Context
import com.example.quranapp.domain.manager.AudioPlayerManager
import com.example.quranapp.domain.repository.AudioRepository
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Intent
import com.example.quranapp.service.AudioPlayerService

class AudioPlayerManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioRepository: AudioRepository
) : AudioPlayerManager {

    // Using the application context so ExoPlayer can live longer than Activities
    override val player: ExoPlayer = ExoPlayer.Builder(context).build()
    
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null
    
    // Default reciter until we implement active reciter tracking deeper if necessary
    private var activeReciterId: String = "mishary_alfasy"

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _currentSurah = MutableStateFlow(1)
    override val currentSurah: StateFlow<Int> = _currentSurah.asStateFlow()

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (isPlaying) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }
            
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _duration.value = player.duration.coerceAtLeast(0L)
                } else if (playbackState == Player.STATE_ENDED) {
                    // Auto-play next Surah
                    scope.launch { next() }
                }
            }
        })
    }

    override suspend fun playSurah(reciterId: String, surahNumber: Int) {
        val url = audioRepository.getAudioUrl(reciterId, surahNumber)
        val mediaItem = MediaItem.fromUri(url)
        
        activeReciterId = reciterId
        _currentSurah.value = surahNumber
        
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        try {
            val intent = Intent(context, AudioPlayerService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun pause() {
        player.pause()
    }

    override fun play() {
        player.play()
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    override suspend fun next() {
        val nextSurah = (_currentSurah.value % 114) + 1
        playSurah(activeReciterId, nextSurah)
    }

    override suspend fun previous() {
        val currentS = _currentSurah.value
        val prevSurah = if (currentS == 1) 114 else currentS - 1
        playSurah(activeReciterId, prevSurah)
    }

    override fun release() {
        stopProgressTracker()
        player.release()
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                _currentPosition.value = player.currentPosition
                _duration.value = player.duration.coerceAtLeast(0L)
                delay(1000L) // Update every second
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
    }
}
