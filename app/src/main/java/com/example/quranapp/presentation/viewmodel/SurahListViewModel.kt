package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.usecase.GetAllSurahsUseCase
import com.example.quranapp.domain.usecase.GetJuzBoundariesUseCase
import com.example.quranapp.domain.model.Ayah
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurahListViewModel @Inject constructor(
    private val getAllSurahsUseCase: GetAllSurahsUseCase,
    private val getJuzBoundariesUseCase: GetJuzBoundariesUseCase
) : ViewModel() {

    private val _surahs = MutableStateFlow<List<com.example.quranapp.domain.model.Surah>>(emptyList())
    val surahs: StateFlow<List<com.example.quranapp.domain.model.Surah>> = _surahs.asStateFlow()

    private val _juzBoundaries = MutableStateFlow<List<Ayah>>(emptyList())
    val juzBoundaries: StateFlow<List<Ayah>> = _juzBoundaries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadSurahs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                android.util.Log.d("SurahListViewModel", "Started fetching full Quran...")
                // Fetch surahs (this will trigger the massive Full Quran download if DB is empty)
                _surahs.value = getAllSurahsUseCase()
                android.util.Log.d("SurahListViewModel", "Fetched surahs: ${_surahs.value.size}")
                
                // Fetch the boundaries (1st ayah of each juz)
                _juzBoundaries.value = getJuzBoundariesUseCase()
                android.util.Log.d("SurahListViewModel", "Fetched boundaries: ${_juzBoundaries.value.size}")
            } catch (e: Exception) {
                android.util.Log.e("SurahListViewModel", "Error loading surahs", e)
                _errorMessage.value = e.message ?: e.toString()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

