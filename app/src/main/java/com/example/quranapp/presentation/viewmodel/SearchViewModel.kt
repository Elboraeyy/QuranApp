package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.Ayah
import com.example.quranapp.domain.model.Hadith
import com.example.quranapp.domain.model.Surah
import com.example.quranapp.domain.usecase.SearchHadithUseCase
import com.example.quranapp.domain.usecase.SearchQuranUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchQuranUseCase: SearchQuranUseCase,
    private val searchHadithUseCase: SearchHadithUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _surahResults = MutableStateFlow<List<Surah>>(emptyList())
    val surahResults: StateFlow<List<Surah>> = _surahResults.asStateFlow()

    private val _ayahResults = MutableStateFlow<List<Ayah>>(emptyList())
    val ayahResults: StateFlow<List<Ayah>> = _ayahResults.asStateFlow()

    private val _hadithResults = MutableStateFlow<List<Hadith>>(emptyList())
    val hadithResults: StateFlow<List<Hadith>> = _hadithResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun updateQuery(value: String) {
        _query.value = value
        if (value.length >= 2) {
            search(value)
        } else {
            _surahResults.value = emptyList()
            _ayahResults.value = emptyList()
            _hadithResults.value = emptyList()
        }
    }

    private fun search(q: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _surahResults.value = searchQuranUseCase.searchSurahs(q)
                _ayahResults.value = searchQuranUseCase.searchAyat(q)
                _hadithResults.value = searchHadithUseCase(q)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
