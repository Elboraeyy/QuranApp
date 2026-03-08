package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.Bookmark
import com.example.quranapp.domain.model.TafsirText
import com.example.quranapp.domain.repository.BookmarkRepository
import com.example.quranapp.domain.repository.TafsirRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TafsirDetailViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val tafsirRepository: TafsirRepository
) : ViewModel() {

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    private val _tafsirTextList = MutableStateFlow<List<TafsirText>>(emptyList())
    val tafsirTextList: StateFlow<List<TafsirText>> = _tafsirTextList.asStateFlow()

    private val _isLoadingTafsir = MutableStateFlow(true)
    val isLoadingTafsir: StateFlow<Boolean> = _isLoadingTafsir.asStateFlow()

    private val _tafsirError = MutableStateFlow<String?>(null)
    val tafsirError: StateFlow<String?> = _tafsirError.asStateFlow()

    private var currentSurahNumber: Int = 1

    fun checkIfBookmarked(surahNumber: Int) {
        currentSurahNumber = surahNumber
        viewModelScope.launch {
            val bookmark = bookmarkRepository.getBookmark(surahNumber, 1) // Tafsir is mostly per surah, so using ayah 1 as a baseline or just Surah bookmark
            _isBookmarked.value = bookmark != null
        }
    }

    fun loadTafsir(surahNumber: Int, tafsirEdition: String = "ar.jalalayn") {
        viewModelScope.launch {
            _isLoadingTafsir.value = true
            _tafsirError.value = null
            try {
                val texts = tafsirRepository.getTafsirTextsBySurah(surahNumber, tafsirEdition)
                _tafsirTextList.value = texts
            } catch (e: Exception) {
                _tafsirError.value = e.message ?: "حدث خطأ أثناء تحميل التفسير"
            } finally {
                _isLoadingTafsir.value = false
            }
        }
    }

    fun toggleBookmark(surahName: String) {
        viewModelScope.launch {
            if (_isBookmarked.value) {
                // If we try to delete, we pass ayahNumber = 1 to match the insertion
                val existingBookmark = bookmarkRepository.getBookmark(currentSurahNumber, 1)
                existingBookmark?.let { bookmarkRepository.deleteBookmark(it) }
                _isBookmarked.value = false
            } else {
                bookmarkRepository.addBookmark(
                    Bookmark(
                        surahNumber = currentSurahNumber,
                        ayahNumber = 1,
                        page = 1,
                        note = "تفسير سورة $surahName"
                    )
                )
                _isBookmarked.value = true
            }
        }
    }
}
