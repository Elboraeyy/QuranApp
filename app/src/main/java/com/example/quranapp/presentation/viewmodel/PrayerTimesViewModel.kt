package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.PrayerTime
import com.example.quranapp.domain.repository.PrayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    private val repository: PrayerRepository
) : ViewModel() {

    private val _today = MutableStateFlow(PrayerTime("", "", "", "", "", "", ""))
    val today: StateFlow<PrayerTime> = _today.asStateFlow()

    init {
        viewModelScope.launch {
            _today.value = repository.getTodayPrayerTime(0.0, 0.0, 1)
        }
    }
}
