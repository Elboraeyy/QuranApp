package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurahDetailViewModel @Inject constructor(
    private val quranRepository: QuranRepository
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

    var isBookmarked = false

    fun loadSurah(surahNumber: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _surah.value = quranRepository.getSurahByNumber(surahNumber)
                _ayahs.value = quranRepository.getAyahsBySurah(surahNumber)
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

    fun toggleBookmark() {
        isBookmarked = !isBookmarked
    }
}

