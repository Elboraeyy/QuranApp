package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    val viewModel: com.example.quranapp.presentation.viewmodel.FavoritesViewModel = hiltViewModel()
    val favorites by viewModel.favorites.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Favorites") })
        },
        bottomBar = {
            BottomNavigationBar(navController, Screen.Favorites.route)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favorites) { fav ->
                ListItem(
                    headlineContent = { Text(fav.type.name) },
                    supportingContent = { Text("${fav.surahNumber ?: ""} ${fav.ayahNumber ?: ""}") }
                )
            }
        }
    }
}

