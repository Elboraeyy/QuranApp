package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.PrayerTime
import com.example.quranapp.domain.repository.PrayerRepository
import com.example.quranapp.domain.location.LocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import android.content.Context
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    private val repository: PrayerRepository,
    private val locationTracker: LocationTracker,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _today = MutableStateFlow(PrayerTime("", "", "", "", "", "", ""))
    val today: StateFlow<PrayerTime> = _today.asStateFlow()

    private val _locationName = MutableStateFlow("القاهرة، مصر")
    val locationName: StateFlow<String> = _locationName.asStateFlow()

    init {
        fetchPrayerTimes()
    }

    fun fetchPrayerTimes() {
        viewModelScope.launch {
            val location = locationTracker.getCurrentLocation()
            val lat = location?.latitude ?: 30.0444 // Default Cairo
            val lng = location?.longitude ?: 31.2357 // Default Cairo
            
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.subAdminArea ?: ""
                    val country = address.countryName ?: ""
                    _locationName.value = if (city.isNotEmpty()) "$city، $country" else country
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _today.value = repository.getTodayPrayerTime(lat, lng, 5) // 5 for Egyptian General Authority of Survey
        }
    }
}
