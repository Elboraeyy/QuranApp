package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.PrayerTime
import com.example.quranapp.domain.repository.PrayerRepository
import com.example.quranapp.domain.repository.SettingsRepository
import com.example.quranapp.service.LocationProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: PrayerRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _today = MutableStateFlow(PrayerTime("", "", "", "", "", "", ""))
    val today: StateFlow<PrayerTime> = _today.asStateFlow()

    init {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val location = LocationProvider(context).getLastKnownLocation()
            val method = settingsRepository.getSettings().calculationMethod
            val lat = location?.latitude ?: 0.0
            val lon = location?.longitude ?: 0.0
            val times = try {
                repository.getTodayPrayerTime(lat, lon, method)
            } catch (e: Exception) {
                PrayerTime("", "--:--", "--:--", "--:--", "--:--", "--:--", "--:--")
            }
            _today.value = times
        }
    }
}
