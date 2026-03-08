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

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    val viewModel: com.example.quranapp.presentation.viewmodel.SearchViewModel = hiltViewModel()
    val query by viewModel.query.collectAsState()
    val surahResults by viewModel.surahResults.collectAsState()
    val ayahResults by viewModel.ayahResults.collectAsState()
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Invisible placeholder to center title
                Spacer(modifier = Modifier.size(44.dp))

                Text(
                    text = "البحث",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Back Button (Right in RTL)
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GreenPrimaryLight
                        )
                    }
                }
            }
            
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.updateQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFC9A24D),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    placeholder = { Text("ابحث في السور والآيات...", style = MaterialTheme.typography.bodyMedium) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    if (surahResults.isNotEmpty()) {
                        item { 
                            Text(
                                "السور", 
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) 
                        }
                        items(surahResults) { surah ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 2.dp,
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ListItem(
                                    headlineContent = { 
                                        Text(
                                            surah.nameArabic,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontFamily = com.example.quranapp.presentation.ui.theme.ScheherazadeNew,
                                            color = MaterialTheme.colorScheme.onSurface 
                                        ) 
                                    },
                                    supportingContent = { Text(surah.nameEnglish, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) },
                                    trailingContent = {
                                        Text("${surah.numberOfAyahs} آيات", color = GreenPrimaryLight, fontWeight = FontWeight.Bold)
                                    },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                            }
                        }
                    }
                    
                    if (ayahResults.isNotEmpty()) {
                        item { 
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "الآيات", 
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) 
                        }
                        items(ayahResults) { ayah ->
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 4.dp,
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text(
                                        text = ayah.textUthmani,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontFamily = com.example.quranapp.presentation.ui.theme.ScheherazadeNew,
                                        lineHeight = 36.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "سورة ${ayah.surahNumber} - آية ${ayah.numberInSurah}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = GreenPrimaryLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

