package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fine && !coarse) {
            (context as? Activity)?.let {
                ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1001)
            }
        }
    }
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

