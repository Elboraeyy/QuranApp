package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(navController: NavController) {
    val viewModel: com.example.quranapp.presentation.viewmodel.ProgressViewModel = hiltViewModel()
    val streak by viewModel.streak.collectAsState()
    val pages by viewModel.pagesToday.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Progress") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Reading Progress", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Current Streak: ${streak} days")
            Text("Pages Read Today: ${pages}")
        }
    }
}

