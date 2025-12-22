package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.repository.QuranRepository
import com.example.quranapp.domain.repository.SettingsRepository
import com.example.quranapp.domain.repository.TranslationRepository
import com.example.quranapp.domain.repository.TafsirRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurahDetailViewModel @Inject constructor(
    private val quranRepository: QuranRepository,
    private val settingsRepository: SettingsRepository,
    private val translationRepository: TranslationRepository,
    private val tafsirRepository: TafsirRepository
) : ViewModel() {

    private val _surah = MutableStateFlow<com.example.quranapp.domain.model.Surah?>(null)
    val surah: StateFlow<com.example.quranapp.domain.model.Surah?> = _surah.asStateFlow()

    private val _ayahs = MutableStateFlow<List<com.example.quranapp.domain.model.Ayah>>(emptyList())
    val ayahs: StateFlow<List<com.example.quranapp.domain.model.Ayah>> = _ayahs.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    var isBookmarked = false

    private val _translationTexts = MutableStateFlow<List<com.example.quranapp.domain.model.TranslationText>>(emptyList())
    val translationTexts: StateFlow<List<com.example.quranapp.domain.model.TranslationText>> = _translationTexts.asStateFlow()

    private val _tafsirTexts = MutableStateFlow<List<com.example.quranapp.domain.model.TafsirText>>(emptyList())
    val tafsirTexts: StateFlow<List<com.example.quranapp.domain.model.TafsirText>> = _tafsirTexts.asStateFlow()

    fun loadSurah(surahNumber: Int) {
        viewModelScope.launch {
            _surah.value = quranRepository.getSurahByNumber(surahNumber)
            _ayahs.value = quranRepository.getAyahsBySurah(surahNumber)
            val settings = settingsRepository.getSettings()
            settings.selectedTranslation?.let {
                _translationTexts.value = translationRepository.getTranslationTextsBySurah(surahNumber, it)
            }
            settings.selectedTafsir?.let {
                _tafsirTexts.value = tafsirRepository.getTafsirTextsBySurah(surahNumber, it)
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

