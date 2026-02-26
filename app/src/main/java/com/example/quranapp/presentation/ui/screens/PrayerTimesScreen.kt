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
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.components.BottomNavigationBar
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import com.example.quranapp.presentation.ui.theme.spacing
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(navController: NavController) {
    val spacing = MaterialTheme.spacing
    
    // Using a simple state for demonstration. In a real app, this comes from ViewModel/Room.
    var prayers by remember {
        mutableStateOf(
            listOf(
                PrayerItemData("الفجر", "05:13 ص", Icons.Default.WbCloudy, true),
                PrayerItemData("الشروق", "06:46 ص", Icons.Default.WbSunny, false, isPrayer = false),
                PrayerItemData("الظهر", "11:50 ص", Icons.Default.WbSunny, true),
                PrayerItemData("العصر", "02:36 م", Icons.Default.WbCloudy, false),
                PrayerItemData("المغرب", "4:00 م", Icons.Default.Mosque, true),
                PrayerItemData("العشاء", "6:19 م", Icons.Default.NightlightRound, false)
            )
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, Screen.PrayerTimes.route)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header with Date Nav and Settings
            PrayerTrackingHeader()
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.gridMargin),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp), // Bottom padding for FAB overlap
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // History Button
                item {
                    HistoryButton()
                }
                
                // Prayer List
                items(prayers) { prayer ->
                    PrayerTrackingItem(
                        item = prayer,
                        onCheckedChange = { isChecked ->
                            prayers = prayers.map {
                                if (it.name == prayer.name) it.copy(isCompleted = isChecked) else it
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PrayerTrackingHeader() {
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
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.size(40.dp)
        ) {
            IconButton(onClick = { /* Open Settings */ }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = GreenPrimaryLight
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
                text = "الثلاثاء 26 جمادى الثاني 1447 هـ",
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
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth().clickable { /* Navigate to History */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History, // Placeholder for tracking icon
                    contentDescription = "Prayer History",
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "سجل الصلوات",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
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
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Right side (RTL) - Icon and Name
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.name,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Left side (RTL) - Time and Checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.time,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            
            if (item.isPrayer) {
                Checkbox(
                    checked = item.isCompleted,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = GreenPrimaryLight,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    )
                )
            } else {
                // Placeholder space to keep alignment for Sunrise
                Spacer(modifier = Modifier.size(48.dp)) // Approximate checkbox size
            }
        }
    }
}

data class PrayerItemData(
    val name: String,
    val time: String,
    val icon: ImageVector,
    val isCompleted: Boolean = false,
    val isPrayer: Boolean = true // False for Sunrise
)

