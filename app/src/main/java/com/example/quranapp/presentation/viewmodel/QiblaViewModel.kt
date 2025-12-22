package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.repository.PrayerRepository
import com.example.quranapp.domain.repository.SettingsRepository
import com.example.quranapp.service.CompassSensor
import com.example.quranapp.service.LocationProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import kotlinx.coroutines.flow.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QiblaViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: PrayerRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _direction = MutableStateFlow(0.0)
    val direction: StateFlow<Double> = _direction.asStateFlow()

    init {
        viewModelScope.launch {
            val loc = LocationProvider(context).getLastKnownLocation()
            val bearing = repository.getQiblaDirection(loc?.latitude ?: 0.0, loc?.longitude ?: 0.0)
            CompassSensor(context).azimuthFlow().combine(kotlinx.coroutines.flow.flowOf(bearing)) { azimuth, target ->
                ((target - azimuth + 360.0) % 360.0)
            }.collect { _direction.value = it }
        }
    }
}
