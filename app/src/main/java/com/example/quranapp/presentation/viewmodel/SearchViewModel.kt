package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.Ayah
import com.example.quranapp.domain.model.Surah
import com.example.quranapp.domain.usecase.SearchQuranUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchQuranUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _surahResults = MutableStateFlow<List<Surah>>(emptyList())
    val surahResults: StateFlow<List<Surah>> = _surahResults.asStateFlow()

    private val _ayahResults = MutableStateFlow<List<Ayah>>(emptyList())
    val ayahResults: StateFlow<List<Ayah>> = _ayahResults.asStateFlow()

    fun updateQuery(value: String) {
        _query.value = value
        search(value)
    }

    private fun search(q: String) {
        viewModelScope.launch {
            _surahResults.value = searchUseCase.searchSurahs(q)
            _ayahResults.value = searchUseCase.searchAyat(q)
        }
    }
}
