package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.AppSettings
import com.example.quranapp.domain.model.FontFamily
import com.example.quranapp.domain.model.ThemeMode
import com.example.quranapp.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getSettingsFlow().collectLatest { _settings.value = it }
        }
    }

    fun updateTheme(theme: ThemeMode) {
        viewModelScope.launch { repository.updateTheme(theme) }
    }

    fun updateFontSize(size: Float) {
        viewModelScope.launch { repository.updateFontSize(size) }
    }

    fun updateFontFamily(fontFamily: FontFamily) {
        viewModelScope.launch { repository.updateFontFamily(fontFamily) }
    }
}
