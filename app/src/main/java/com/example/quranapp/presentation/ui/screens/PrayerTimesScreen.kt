package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.components.BottomNavigationBar
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import com.example.quranapp.presentation.ui.theme.spacing
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quranapp.presentation.viewmodel.PrayerTimesViewModel
import com.example.quranapp.presentation.viewmodel.SettingsViewModel
import com.example.quranapp.domain.model.AdhanPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    navController: NavController,
    prayerViewModel: PrayerTimesViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val spacing = MaterialTheme.spacing
    
    val todayPrayerTime by prayerViewModel.today.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()
    val locationName by prayerViewModel.locationName.collectAsState()

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

    val prayers = remember(todayPrayerTime, settings) {
        listOf(
            PrayerItemData("الفجر", formatTo12Hour(todayPrayerTime.fajr), Icons.Default.WbCloudy, 1, settings.fajrPreference),
            PrayerItemData("الشروق", formatTo12Hour(todayPrayerTime.sunrise), Icons.Default.WbSunny, 2, settings.sunrisePreference, isPrayer = false),
            PrayerItemData("الظهر", formatTo12Hour(todayPrayerTime.dhuhr), Icons.Default.WbSunny, 3, settings.dhuhrPreference),
            PrayerItemData("العصر", formatTo12Hour(todayPrayerTime.asr), Icons.Default.WbCloudy, 4, settings.asrPreference),
            PrayerItemData("المغرب", formatTo12Hour(todayPrayerTime.maghrib), Icons.Default.Mosque, 5, settings.maghribPreference),
            PrayerItemData("العشاء", formatTo12Hour(todayPrayerTime.isha), Icons.Default.NightlightRound, 6, settings.ishaPreference)
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                PrayerTrackingHeader(todayPrayerTime.date)
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = spacing.gridMargin),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        HistoryButton()
                    }
                    
                    items(prayers) { prayer ->
                        PrayerTrackingItem(
                            item = prayer,
                            isCompleted = completedStates[prayer.name] ?: false,
                            onCheckedChange = { isChecked ->
                                completedStates = completedStates.toMutableMap().apply { put(prayer.name, isChecked) }
                            },
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
            
            BottomNavigationBar(
                navController = navController,
                currentRoute = Screen.PrayerTimes.route,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun PrayerTrackingHeader(dateText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Settings Icon (Left in RTL)
        Surface(
            shape = CircleShape,
            color = Color(0xFFC9A24D).copy(alpha = 0.15f),
            modifier = Modifier.size(44.dp)
        ) {
            IconButton(onClick = { /* Open Settings */ }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = GreenPrimaryLight,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }

        // Date Navigator (Center)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Previous Day */ }) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Previous Day", tint = GreenPrimaryLight)
            }
            Text(
                text = dateText.ifEmpty { "الثلاثاء 26 جمادى الثاني 1447 هـ" },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { /* Next Day */ }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Next Day", tint = GreenPrimaryLight)
            }
        }
    }
}

@Composable
fun HistoryButton() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth().clickable { /* Navigate to History */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History, // Placeholder for tracking icon
                        contentDescription = "Prayer History",
                        tint = GreenPrimaryLight,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "سجل الصلوات",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "View",
                tint = GreenPrimaryLight
            )
        }
    }
}

@Composable
fun PrayerTrackingItem(
    item: PrayerItemData,
    isCompleted: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onPreferenceChange: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Right side (RTL) - Icon and Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = GreenPrimaryLight.copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.name,
                        tint = GreenPrimaryLight,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = com.example.quranapp.presentation.ui.theme.ScheherazadeNew,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 22.sp
                )
            }

            // Left side (RTL) - Time and Checkbox
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.time,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimaryLight
                )
                Spacer(modifier = Modifier.width(8.dp))
                
                if (item.isPrayer) {
                    IconButton(onClick = onPreferenceChange) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Transparent,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = when (item.preference) {
                                    AdhanPreference.ADHAN -> Icons.Default.NotificationsActive
                                    AdhanPreference.NOTIFICATION -> Icons.Default.Notifications
                                    AdhanPreference.NONE -> Icons.Default.NotificationsOff
                                },
                                contentDescription = "Notification Preference",
                                tint = if (item.preference != AdhanPreference.NONE) Color(0xFFC9A24D) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = onCheckedChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = GreenPrimaryLight,
                            uncheckedColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                } else {
                    IconButton(onClick = onPreferenceChange) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Transparent,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = when (item.preference) {
                                    AdhanPreference.ADHAN -> Icons.Default.NotificationsActive
                                    AdhanPreference.NOTIFICATION -> Icons.Default.Notifications
                                    AdhanPreference.NONE -> Icons.Default.NotificationsOff
                                },
                                contentDescription = "Notification Preference",
                                tint = if (item.preference != AdhanPreference.NONE) Color(0xFFC9A24D) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(48.dp)) // Approximate checkbox size
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

