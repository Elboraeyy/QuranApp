package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.Bookmark
import com.example.quranapp.domain.repository.BookmarkRepository
import com.example.quranapp.domain.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurahDetailViewModel @Inject constructor(
    private val quranRepository: QuranRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _surah = MutableStateFlow<com.example.quranapp.domain.model.Surah?>(null)
    val surah: StateFlow<com.example.quranapp.domain.model.Surah?> = _surah.asStateFlow()

    private val _ayahs = MutableStateFlow<List<com.example.quranapp.domain.model.Ayah>>(emptyList())
    val ayahs: StateFlow<List<com.example.quranapp.domain.model.Ayah>> = _ayahs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    private var currentSurahNumber: Int = 1

    fun loadSurah(surahNumber: Int) {
        currentSurahNumber = surahNumber
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _surah.value = quranRepository.getSurahByNumber(surahNumber)
                _ayahs.value = quranRepository.getAyahsBySurah(surahNumber)
                checkIfBookmarked()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load Ayahs"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun checkIfBookmarked() {
        // Here we track bookmarking the entire Surah by marking ayah 1.
        _isBookmarked.value = bookmarkRepository.isBookmarked(currentSurahNumber, 1)
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            if (_isBookmarked.value) {
                // To remove we need the specific bookmark object or we can use the helper we added (if it exists)
                val existing = bookmarkRepository.getBookmark(currentSurahNumber, 1)
                existing?.let { bookmarkRepository.deleteBookmark(it) }
                _isBookmarked.value = false
            } else {
                bookmarkRepository.addBookmark(
                    Bookmark(surahNumber = currentSurahNumber, ayahNumber = 1, page = 1)
                )
                _isBookmarked.value = true
            }
        }
    }
}

