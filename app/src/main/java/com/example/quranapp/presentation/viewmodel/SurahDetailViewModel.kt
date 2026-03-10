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

    // --- Old API for SurahDetailScreen ---
    private val _ayahs = MutableStateFlow<List<com.example.quranapp.domain.model.Ayah>>(emptyList())
    val ayahs: StateFlow<List<com.example.quranapp.domain.model.Ayah>> = _ayahs.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private var currentSurahNumber: Int = 1

    fun loadSurah(surahNumber: Int) {
        currentSurahNumber = surahNumber
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _surah.value = quranRepository.getSurahByNumber(surahNumber)
                _ayahs.value = quranRepository.getAyahsBySurah(surahNumber)
                checkIfBookmarkedSurah()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load Ayahs"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    private suspend fun checkIfBookmarkedSurah() {
        _isBookmarked.value = bookmarkRepository.isBookmarked(currentSurahNumber, 1)
    }

    // --- New API for QCF QuranReadingScreen ---
    private val _qcfPage = MutableStateFlow<com.example.quranapp.domain.model.QcfPage?>(null)
    val qcfPage: StateFlow<com.example.quranapp.domain.model.QcfPage?> = _qcfPage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    private var currentPageNumber: Int = 1

    fun loadPage(pageNumber: Int) {
        currentPageNumber = pageNumber
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _qcfPage.value = quranRepository.getQcfPage(pageNumber)
                
                val verse = _qcfPage.value?.verses?.firstOrNull()
                if (verse != null) {
                   _surah.value = quranRepository.getSurahByNumber(verse.surahNumber)
                }

                checkIfBookmarkedPage()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load Page"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun checkIfBookmarkedPage() {
        val firstVerse = _qcfPage.value?.verses?.firstOrNull()
        if (firstVerse != null) {
            _isBookmarked.value = bookmarkRepository.isBookmarked(firstVerse.surahNumber, firstVerse.ayahNumber)
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            if (_qcfPage.value != null) {
                val firstVerse = _qcfPage.value?.verses?.firstOrNull() ?: return@launch
                if (_isBookmarked.value) {
                    val existing = bookmarkRepository.getBookmark(firstVerse.surahNumber, firstVerse.ayahNumber)
                    existing?.let { bookmarkRepository.deleteBookmark(it) }
                    _isBookmarked.value = false
                } else {
                    bookmarkRepository.addBookmark(
                        Bookmark(surahNumber = firstVerse.surahNumber, ayahNumber = firstVerse.ayahNumber, page = currentPageNumber)
                    )
                    _isBookmarked.value = true
                }
            } else {
                if (_isBookmarked.value) {
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
}

