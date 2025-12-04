package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel: com.example.quranapp.presentation.viewmodel.SettingsViewModel = hiltViewModel()
    val settings by viewModel.settings.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        },
        bottomBar = {
            BottomNavigationBar(navController, Screen.Settings.route)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Theme", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.updateTheme(com.example.quranapp.domain.model.ThemeMode.LIGHT) }) { Text("Light") }
                Button(onClick = { viewModel.updateTheme(com.example.quranapp.domain.model.ThemeMode.DARK) }) { Text("Dark") }
                Button(onClick = { viewModel.updateTheme(com.example.quranapp.domain.model.ThemeMode.SYSTEM) }) { Text("System") }
            }
            Text("Font Size: ${settings.fontSize}", style = MaterialTheme.typography.titleMedium)
            Slider(value = settings.fontSize, onValueChange = { viewModel.updateFontSize(it) }, valueRange = 14f..28f)
            Text("Font Family", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.updateFontFamily(com.example.quranapp.domain.model.FontFamily.UTHMANI) }) { Text("Uthmani") }
                Button(onClick = { viewModel.updateFontFamily(com.example.quranapp.domain.model.FontFamily.INDO_PAK) }) { Text("IndoPak") }
            }
        }
    }
}

