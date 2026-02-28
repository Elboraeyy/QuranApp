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
import com.example.quranapp.domain.location.LocationTracker
import com.example.quranapp.domain.qibla.QiblaTracker
import kotlin.math.*

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val locationTracker: LocationTracker,
    private val qiblaTracker: QiblaTracker
) : ViewModel() {

    private val _direction = MutableStateFlow(0f)
    val direction: StateFlow<Float> = _direction.asStateFlow()

    private val _distance = MutableStateFlow(0)
    val distance: StateFlow<Int> = _distance.asStateFlow()

    private val _qiblaAngle = MutableStateFlow(0f)
    val qiblaAngle: StateFlow<Float> = _qiblaAngle.asStateFlow()

    init {
        viewModelScope.launch { 
            // 1. Get User Location
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                val qiblaLat = 21.422487
                val qiblaLng = 39.826206

                // Calculate bearing to Mecca
                val lat1 = Math.toRadians(location.latitude)
                val lng1 = Math.toRadians(location.longitude)
                val lat2 = Math.toRadians(qiblaLat)
                val lng2 = Math.toRadians(qiblaLng)

                val dLng = lng2 - lng1
                val y = sin(dLng) * cos(lat2)
                val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLng)
                var bearing = Math.toDegrees(atan2(y, x)).toFloat()
                bearing = (bearing + 360) % 360

                _qiblaAngle.value = bearing

                // Calculate distance using Haversine
                val r = 6371 // Radius of earth in KM
                val dLat = lat2 - lat1
                val a = sin(dLat / 2) * sin(dLat / 2) + cos(lat1) * cos(lat2) * sin(dLng / 2) * sin(dLng / 2)
                val c = 2 * atan2(sqrt(a), sqrt(1 - a))
                _distance.value = (r * c).toInt()
            }
            
            // 2. Start tracking device orientation and combine with qibla angle
            qiblaTracker.startTracking().collect { azimuth ->
                // The compass view needs to rotate negatively.
                // The Kaaba arrow points to the _qiblaAngle relative to North.
                // Right now we just pass azimuth, we'll reconcile in UI.
                _direction.value = azimuth
            }
        }
    }
}
