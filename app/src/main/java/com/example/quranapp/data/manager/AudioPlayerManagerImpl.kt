package com.example.quranapp.data.manager

import android.content.Context
import com.example.quranapp.domain.manager.AudioPlayerManager
import com.example.quranapp.domain.model.AyahRef
import com.example.quranapp.domain.repository.AudioRepository
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    override val player: ExoPlayer = ExoPlayer.Builder(context).build()
    
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null
    
    private var activeReciterId: String = "mishary_alfasy"
    private var isPlayingPage: Boolean = false
    private var currentPageAyahs: List<AyahRef> = emptyList()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _currentSurah = MutableStateFlow(1)
    override val currentSurah: StateFlow<Int> = _currentSurah.asStateFlow()

    private val _currentAyahIndex = MutableStateFlow(-1)
    override val currentAyahIndex: StateFlow<Int> = _currentAyahIndex.asStateFlow()

    private val _pageCompleted = MutableSharedFlow<Unit>()
    override val pageCompleted: SharedFlow<Unit> = _pageCompleted.asSharedFlow()

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
                if (playing) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }
            
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (isPlayingPage) {
                    _currentAyahIndex.value = player.currentMediaItemIndex
                }
            }
            
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _duration.value = player.duration.coerceAtLeast(0L)
                } else if (playbackState == Player.STATE_ENDED) {
                    if (isPlayingPage) {
                        // Page playback completed — signal auto-page-turn
                        isPlayingPage = false
                        _currentAyahIndex.value = -1
                        scope.launch { _pageCompleted.emit(Unit) }
                    } else {
                        // Auto-play next Surah (existing behavior)
                        scope.launch { next() }
                    }
                }
            }
        })
    }

    override suspend fun playSurah(reciterId: String, surahNumber: Int) {
        isPlayingPage = false
        _currentAyahIndex.value = -1
        currentPageAyahs = emptyList()
        
        val url = audioRepository.getAudioUrl(reciterId, surahNumber)
        val mediaItem = MediaItem.fromUri(url)
        
        activeReciterId = reciterId
        _currentSurah.value = surahNumber
        
        player.clearMediaItems()
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        startForegroundService()
    }

    override suspend fun playPageAyahs(reciterId: String, ayahs: List<AyahRef>) {
        if (ayahs.isEmpty()) return
        
        isPlayingPage = true
        activeReciterId = reciterId
        currentPageAyahs = ayahs
        _currentSurah.value = ayahs.first().surahNumber
        
        player.clearMediaItems()
        
        ayahs.forEach { ayah ->
            val url = audioRepository.getAyahAudioUrl(reciterId, ayah.surahNumber, ayah.ayahNumber)
            player.addMediaItem(MediaItem.fromUri(url))
        }
        
        _currentAyahIndex.value = 0
        player.prepare()
        player.play()

        startForegroundService()
    }

    override suspend fun playAyah(reciterId: String, surahNumber: Int, ayahNumber: Int) {
        isPlayingPage = false
        _currentAyahIndex.value = -1
        currentPageAyahs = emptyList()
        activeReciterId = reciterId
        _currentSurah.value = surahNumber
        
        val url = audioRepository.getAyahAudioUrl(reciterId, surahNumber, ayahNumber)
        player.clearMediaItems()
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.play()
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
        if (isPlayingPage && player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
        } else {
            val nextSurah = (_currentSurah.value % 114) + 1
            playSurah(activeReciterId, nextSurah)
        }
    }

    override suspend fun previous() {
        if (isPlayingPage && player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem()
        } else {
            val currentS = _currentSurah.value
            val prevSurah = if (currentS == 1) 114 else currentS - 1
            playSurah(activeReciterId, prevSurah)
        }
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
                delay(500L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
    }

    private fun startForegroundService() {
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
}
