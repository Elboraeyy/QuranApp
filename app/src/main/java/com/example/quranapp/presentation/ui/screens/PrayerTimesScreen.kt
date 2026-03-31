package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.components.BottomNavigationBar
import com.example.quranapp.presentation.ui.theme.*
import com.example.quranapp.presentation.viewmodel.PrayerTimesViewModel
import com.example.quranapp.presentation.viewmodel.SettingsViewModel
import com.example.quranapp.domain.model.AdhanPreference
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    navController: NavController,
    viewModel: PrayerTimesViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val spacing = MaterialTheme.spacing
    val selectedDate by viewModel.selectedDate.collectAsState()
    val todayPrayer by viewModel.today.collectAsState()
    val selectedDayPrayer by viewModel.selectedDayPrayer.collectAsState()
    val nextPrayerName by viewModel.nextPrayerName.collectAsState()
    val timeUntilNext by viewModel.timeUntilNextPrayer.collectAsState()
    val locationName by viewModel.locationName.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()

    // Using a simple state for demonstration. In a real app, this comes from ViewModel/Room.
    var completedStates by remember { mutableStateOf(mapOf<String, Boolean>()) }

    // Helper to format 24h to 12h
    fun formatTo12Hour(timeStr: String): String {
        if (timeStr == "00:00" || timeStr.contains("--")) return timeStr
        try {
            val cleanTime = timeStr.substringBefore(" ").trim()
            val time = java.time.LocalTime.parse(cleanTime, java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
            return time.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale("ar")))
        } catch (e: Exception) {
            return timeStr
        }
    }

    val prayers = remember(selectedDayPrayer, settings) {
        selectedDayPrayer?.let { day ->
            listOf(
                PrayerItemData("الفجر", formatTo12Hour(day.fajr), Icons.Default.WbCloudy, 1, settings.fajrPreference),
                PrayerItemData("الشروق", formatTo12Hour(day.sunrise), Icons.Default.WbSunny, 2, settings.sunrisePreference, isPrayer = false),
                PrayerItemData("الظهر", formatTo12Hour(day.dhuhr), Icons.Default.WbSunny, 3, settings.dhuhrPreference),
                PrayerItemData("العصر", formatTo12Hour(day.asr), Icons.Default.WbCloudy, 4, settings.asrPreference),
                PrayerItemData("المغرب", formatTo12Hour(day.maghrib), Icons.Default.Mosque, 5, settings.maghribPreference),
                PrayerItemData("العشاء", formatTo12Hour(day.isha), Icons.Default.NightlightRound, 6, settings.ishaPreference)
            )
        } ?: emptyList()
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = Screen.PrayerTimes.route
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                PremiumPrayerHeader(
                    location = locationName,
                    nextPrayer = nextPrayerName ?: "...",
                    timeRemaining = timeUntilNext ?: "--:--:--",
                    hijriDate = selectedDayPrayer?.date ?: ""
                )
                
                DateNavigator(
                    selectedDate = selectedDate,
                    onDateSelected = { viewModel.navigateDate(it) }
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(prayers) { prayer ->
                        PremiumPrayerCard(
                            item = prayer,
                            isNext = prayer.name == nextPrayerName && selectedDate == LocalDate.now(),
                            onPreferenceChange = {
                                val nextPref = when (prayer.preference) {
                                    AdhanPreference.ADHAN -> AdhanPreference.NOTIFICATION
                                    AdhanPreference.NOTIFICATION -> AdhanPreference.NONE
                                    AdhanPreference.NONE -> AdhanPreference.ADHAN
                                }
                                settingsViewModel.updateAdhanPreference(prayer.id, nextPref)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumPrayerHeader(
    location: String,
    nextPrayer: String,
    timeRemaining: String,
    hijriDate: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 12.dp
    ) {
        Box {
            // Subtle decorative patterns could go here
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    location,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelMedium
                )
                
                Text(
                    hijriDate,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(32.dp))

                Text(
                    "صلاة $nextPrayer القادمة خلال",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = ScheherazadeNew
                )

                Text(
                    timeRemaining,
                    color = Color.White,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DateNavigator(
    selectedDate: LocalDate,
    onDateSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onDateSelected(1) }) {
            Icon(Icons.Default.KeyboardArrowRight, null, tint = GreenPrimaryLight)
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale("ar"))),
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight
            )
            if (selectedDate != LocalDate.now()) {
                Text(
                    "العودة لليوم",
                    color = GoldSecondaryLight,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { 
                        val diff = LocalDate.now().toEpochDay() - selectedDate.toEpochDay()
                        onDateSelected(diff.toInt())
                    }
                )
            }
        }

        IconButton(onClick = { onDateSelected(-1) }) {
            Icon(Icons.Default.KeyboardArrowLeft, null, tint = GreenPrimaryLight)
        }
    }
}

@Composable
fun PremiumPrayerCard(
    item: PrayerItemData,
    isNext: Boolean,
    onPreferenceChange: () -> Unit
) {
    val backgroundColor = if (isNext) GreenPrimaryLight.copy(alpha = 0.05f) else Color.White
    val borderColor = if (isNext) GoldSecondaryLight.copy(alpha = 0.3f) else Color.Transparent

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large, // Organic 32dp
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (isNext) 8.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isNext) GoldSecondaryLight.copy(alpha = 0.2f) else BackgroundLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        item.icon,
                        null,
                        tint = if (isNext) GreenPrimaryLight else TextPrimaryLight.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(Modifier.width(16.dp))
                
                Column {
                    Text(
                        item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight,
                        fontFamily = ScheherazadeNew,
                        fontSize = 24.sp
                    )
                    if (isNext) {
                        Text(
                            "الصلاة القادمة",
                            color = GreenPrimaryLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.time,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isNext) GreenPrimaryLight else TextPrimaryLight
                )
                
                Spacer(Modifier.width(12.dp))

                IconButton(onClick = onPreferenceChange) {
                    Icon(
                        imageVector = when (item.preference) {
                            AdhanPreference.ADHAN -> Icons.Default.NotificationsActive
                            AdhanPreference.NOTIFICATION -> Icons.Default.Notifications
                            AdhanPreference.NONE -> Icons.Default.NotificationsOff
                        },
                        contentDescription = null,
                        tint = if (item.preference != AdhanPreference.NONE) GoldSecondaryLight else TextPrimaryLight.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

data class PrayerItemData(
    val name: String,
    val time: String,
    val icon: ImageVector,
    val id: Int,
    val preference: AdhanPreference,
    val isPrayer: Boolean = true // False for Sunrise
)

