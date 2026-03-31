package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.PrayerTime
import com.example.quranapp.domain.repository.PrayerRepository
import com.example.quranapp.domain.location.LocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import android.content.Context
import android.location.Geocoder
import com.example.quranapp.data.alarm.AdhanAlarmScheduler
import com.example.quranapp.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    private val repository: PrayerRepository,
    private val locationTracker: LocationTracker,
    private val alarmScheduler: AdhanAlarmScheduler,
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _prayerTimes = MutableStateFlow<List<PrayerTime>>(emptyList())
    val prayerTimes: StateFlow<List<PrayerTime>> = _prayerTimes.asStateFlow()

    private val _today = MutableStateFlow(PrayerTime("", "", "", "", "", "", ""))
    val today: StateFlow<PrayerTime> = _today.asStateFlow()

    private val _selectedDayPrayer = MutableStateFlow<PrayerTime?>(null)
    val selectedDayPrayer: StateFlow<PrayerTime?> = _selectedDayPrayer.asStateFlow()

    private val _locationName = MutableStateFlow("القاهرة، مصر")
    val locationName: StateFlow<String> = _locationName.asStateFlow()

    private val _nextPrayerName = MutableStateFlow<String?>(null)
    val nextPrayerName: StateFlow<String?> = _nextPrayerName.asStateFlow()

    private val _timeUntilNextPrayer = MutableStateFlow<String?>(null)
    val timeUntilNextPrayer: StateFlow<String?> = _timeUntilNextPrayer.asStateFlow()

    init {
        fetchPrayerTimes()
        startCountdown()
    }

    fun navigateDate(days: Int) {
        _selectedDate.value = _selectedDate.value.plusDays(days.toLong())
        updateSelectedDayPrayer()
    }

    private fun updateSelectedDayPrayer() {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            // Check if we have the month for the selected date
            if (_prayerTimes.value.isEmpty() || _selectedDate.value.monthValue != LocalDate.now().monthValue) {
                fetchPrayerTimes(_selectedDate.value.year, _selectedDate.value.monthValue)
            }
            
            // In a real app, the hijri string in PrayerTime might make this tricky.
            // Let's assume for now we can find it by index or we need to refine the model.
            // For now, I'll just use the first one if we only have one, or find it if we have many.
            if (_prayerTimes.value.isNotEmpty()) {
                val dayOfMonth = _selectedDate.value.dayOfMonth
                if (dayOfMonth <= _prayerTimes.value.size) {
                    _selectedDayPrayer.value = _prayerTimes.value[dayOfMonth - 1]
                }
            }
        }
    }

    fun fetchPrayerTimes(year: Int = LocalDate.now().year, month: Int = LocalDate.now().monthValue) {
        viewModelScope.launch {
            val location = locationTracker.getCurrentLocation()
            val lat = location?.latitude ?: 30.0444 // Default Cairo
            val lng = location?.longitude ?: 31.2357 // Default Cairo
            
            val settings = settingsRepository.getSettings()
            val method = settings.calculationMethod
            
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

            val calendarTimes = repository.getPrayerTimes(lat, lng, method, month, year)
            _prayerTimes.value = calendarTimes.times
            
            if (month == LocalDate.now().monthValue && year == LocalDate.now().year) {
                val todayIndex = LocalDate.now().dayOfMonth - 1
                if (todayIndex < calendarTimes.times.size) {
                    _today.value = calendarTimes.times[todayIndex]
                    _selectedDayPrayer.value = _today.value
                    // Schedule Alarms for today
                    alarmScheduler.scheduleAlarms(_today.value)
                }
            } else {
                updateSelectedDayPrayer()
            }
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            while (true) {
                updateNextPrayer()
                delay(1000)
            }
        }
    }

    private fun updateNextPrayer() {
        val prayerTime = _today.value
        if (prayerTime.fajr.isEmpty()) return

        val now = LocalDateTime.now()
        val prayers = listOf(
            "الفجر" to prayerTime.fajr,
            "الشروق" to prayerTime.sunrise,
            "الظهر" to prayerTime.dhuhr,
            "العصر" to prayerTime.asr,
            "المغرب" to prayerTime.maghrib,
            "العشاء" to prayerTime.isha
        )

        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        
        var nextP: Pair<String, String>? = null
        var nextTime: LocalDateTime? = null

        for (p in prayers) {
            try {
                val cleanTime = p.second.substringBefore(" ").trim()
                val pTime = LocalTime.parse(cleanTime, formatter)
                val pDateTime = LocalDateTime.of(now.toLocalDate(), pTime)
                
                if (pDateTime.isAfter(now)) {
                    nextP = p
                    nextTime = pDateTime
                    break
                }
            } catch (_: Exception) {
                continue
            }
        }

        if (nextP == null) {
            // Next prayer is Fajr tomorrow
            // For simplicity, just show "الفجر"
            _nextPrayerName.value = "الفجر"
            _timeUntilNextPrayer.value = "--:--:--"
        } else {
            _nextPrayerName.value = nextP.first
            val diff = now.until(nextTime!!, ChronoUnit.SECONDS)
            val hours = diff / 3600
            val minutes = (diff % 3600) / 60
            val seconds = diff % 60
            _timeUntilNextPrayer.value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }
}
