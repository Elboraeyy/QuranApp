package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(navController: NavController) {
    val viewModel: com.example.quranapp.presentation.viewmodel.PrayerTimesViewModel = hiltViewModel()
    val today by viewModel.today.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prayer Times") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, Screen.PrayerTimes.route)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Prayer Times", style = MaterialTheme.typography.headlineMedium)
            ListItem(headlineContent = { Text("Fajr") }, trailingContent = { Text(today.fajr) })
            ListItem(headlineContent = { Text("Sunrise") }, trailingContent = { Text(today.sunrise) })
            ListItem(headlineContent = { Text("Dhuhr") }, trailingContent = { Text(today.dhuhr) })
            ListItem(headlineContent = { Text("Asr") }, trailingContent = { Text(today.asr) })
            ListItem(headlineContent = { Text("Maghrib") }, trailingContent = { Text(today.maghrib) })
            ListItem(headlineContent = { Text("Isha") }, trailingContent = { Text(today.isha) })
        }
    }
}

