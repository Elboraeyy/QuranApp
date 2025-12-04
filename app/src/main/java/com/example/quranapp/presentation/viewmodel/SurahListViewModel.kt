package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.usecase.GetAllSurahsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurahListViewModel @Inject constructor(
    private val getAllSurahsUseCase: GetAllSurahsUseCase
) : ViewModel() {

    private val _surahs = MutableStateFlow<List<com.example.quranapp.domain.model.Surah>>(emptyList())
    val surahs: StateFlow<List<com.example.quranapp.domain.model.Surah>> = _surahs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadSurahs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _surahs.value = getAllSurahsUseCase()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}

