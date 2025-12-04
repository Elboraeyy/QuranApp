package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.repository.PrayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val repository: PrayerRepository
) : ViewModel() {

    private val _direction = MutableStateFlow(0.0)
    val direction: StateFlow<Double> = _direction.asStateFlow()

    init {
        viewModelScope.launch { _direction.value = repository.getQiblaDirection(0.0, 0.0) }
    }
}
