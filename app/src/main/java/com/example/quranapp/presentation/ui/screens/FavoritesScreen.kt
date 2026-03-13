package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.presentation.ui.theme.spacing
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, viewModel: com.example.quranapp.presentation.viewmodel.FavoritesViewModel = androidx.hilt.navigation.compose.hiltViewModel()) {
    val spacing = MaterialTheme.spacing
    
    val tabs = listOf("الأذكار", "الأحاديث", "القرآن الكريم", "التفسير و المعاني")
    var selectedTabIndex by remember { mutableStateOf(0) }

    val allFavorites by viewModel.favorites.collectAsState()
    val savedAdhkar = allFavorites.filter { it.type == com.example.quranapp.domain.model.FavoriteType.ADHKAR }

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
                    .padding(horizontal = spacing.gridMargin, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search Button (Left)
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(onClick = { /* Search Action */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GreenPrimaryLight,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }

                Text(
                    text = "المفضلة",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Back Button (Right in design)
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Back",
                            tint = GreenPrimaryLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tabs Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gridMargin),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTabIndex == index
                    Surface(
                        shape = RoundedCornerShape(32.dp),
                        color = if (isSelected) GreenPrimaryLight else MaterialTheme.colorScheme.surface,
                        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=0.1f)),
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTabIndex = index }
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 2.dp),
                            maxLines = 1,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content Area depending on Tab
            if (selectedTabIndex == 0) {
                // Adhkar List
                if (savedAdhkar.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "لا توجد أذكار محفوظة",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.gridMargin),
                        contentPadding = PaddingValues(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(savedAdhkar) { favorite ->
                            // Here we ideally need to fetch the actual Adhkar text by ID.
                            // For now, we will use a generic text if we don't have the Adhkar name directly.
                            SavedFavoriteCard(favorite = favorite, onRemove = { viewModel.removeFavorite(favorite) })
                        }
                    }
                }
            } else if (selectedTabIndex == 1) {
                // Hadiths Link
                Box(
                    modifier = Modifier.fillMaxSize().padding(spacing.gridMargin),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = GreenPrimaryLight.copy(alpha = 0.1f),
                        onClick = { navController.navigate(com.example.quranapp.presentation.navigation.Screen.BookmarkedHadiths.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = GreenPrimaryLight,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "عرض الأحاديث المحفوظة",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimaryLight
                            )
                        }
                    }
                }
            } else {
                // Placeholder for other tabs (Quran, Tafsir)
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "لا توجد عناصرحاليا",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun SavedFavoriteCard(favorite: com.example.quranapp.domain.model.Favorite, onRemove: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Icon (Rosary inside green circle)
            Surface(
                shape = CircleShape,
                color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp, // Placeholder for Rosary
                    contentDescription = null,
                    tint = Color(0xFFC9A24D),
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Text
            Text(
                text = "ذكر رقم ${favorite.adhkarId}", // Placeholder until we load actual adhkar text
                style = MaterialTheme.typography.headlineSmall, // Quranic/Arabic Script feel
                fontFamily = com.example.quranapp.presentation.ui.theme.ScheherazadeNew,
                lineHeight = 36.sp,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Actions (Remove Bookmark, Share)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp),
                        onClick = onRemove
                    ) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Remove Saved", tint = GreenPrimaryLight, modifier = Modifier.padding(10.dp))
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = GreenPrimaryLight, modifier = Modifier.padding(10.dp))
                    }
                }
            }
        }
    }
}
