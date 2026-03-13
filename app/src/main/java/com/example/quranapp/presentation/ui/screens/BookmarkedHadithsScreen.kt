package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import com.example.quranapp.presentation.viewmodel.HadithViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkedHadithsScreen(
    navController: NavController,
    viewModel: HadithViewModel = hiltViewModel()
) {
    val allHadiths by viewModel.allHadiths.collectAsState()
    val bookmarkedHadiths by viewModel.bookmarkedHadiths.collectAsState()
    // Alternatively, you can read from the ViewModel by mapping the bookmarked IDs
    // For simplicity here, assuming ViewModel exposes bookmarked hadiths, 
    // or we fetch them using the same UI logic:
    // This is a placeholder for actual bookmark DB read in the ViewModel.
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("الأحاديث المحفوظة", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        if (allHadiths.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimaryLight)
            }
        } else if (bookmarkedHadiths.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    text = "لم تقم بحفظ أي أحاديث بعد.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(bookmarkedHadiths) { hadith ->
                    val isBookmarked by viewModel.isBookmarked(hadith.id).collectAsState(initial = false)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .clickable { navController.navigate(Screen.HadithDetail.createRoute(hadith.id)) }
                    ) {
                        HadithCard(
                            hadith = hadith,
                            isBookmarked = isBookmarked,
                            onBookmark = { viewModel.toggleBookmark(hadith) }
                        )
                    }
                }
            }
        }
    }
}
