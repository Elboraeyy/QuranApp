package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    val viewModel: com.example.quranapp.presentation.viewmodel.SearchViewModel = hiltViewModel()
    val query by viewModel.query.collectAsState()
    val surahResults by viewModel.surahResults.collectAsState()
    val ayahResults by viewModel.ayahResults.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.updateQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search Surahs or Ayat...") }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Text("Surahs", style = MaterialTheme.typography.titleMedium) }
                items(surahResults) { surah ->
                    ListItem(
                        headlineContent = { Text(surah.nameEnglish) },
                        supportingContent = { Text(surah.nameArabic) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingContent = {
                            Text("${surah.numberOfAyahs} Ayahs")
                        }
                    )
                }
                item { Text("Ayat", style = MaterialTheme.typography.titleMedium) }
                items(ayahResults) { ayah ->
                    ListItem(
                        headlineContent = { Text(ayah.textUthmani) },
                        supportingContent = { Text("${ayah.surahNumber}:${ayah.numberInSurah}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

