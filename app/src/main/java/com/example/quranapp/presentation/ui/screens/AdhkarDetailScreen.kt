package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Refresh
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
fun AdhkarDetailScreen(navController: NavController, categoryId: Int = 1) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()

    // Mock specific Dhikr State
    var currentCount by remember { mutableStateOf(0) }
    val targetCount = 3
    val dhikrTitle = "آيـة الكـرسـي"
    val dhikrText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ."

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
                // Refresh Button (Left)
                Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.5f)),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { currentCount = 0 }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Count",
                            tint = GreenPrimaryLight
                        )
                    }
                }

                Text(
                    text = "أذكار الصباح", // Mock category title based on ID
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Back Button (Right in design)
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = spacing.gridMargin),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dhikr Card
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Let it expand naturally to push footer down
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Decorative Icon (Rosary/Misbaha)
                        Surface(
                            shape = CircleShape,
                            color = GreenPrimaryLight.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.3f)),
                            modifier = Modifier.size(56.dp)
                        ) {
                            // Using a placeholder icon resembling the Rosary from design
                            Icon(
                                imageVector = Icons.Default.TouchApp, // Placeholder
                                contentDescription = null,
                                tint = GreenPrimaryLight,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Dhikr Text
                        Text(
                            text = dhikrText,
                            style = MaterialTheme.typography.headlineSmall, // Should use Scheherazade New
                            lineHeight = 36.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Bottom Actions inside Card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Actions (Share, Bookmark)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Transparent,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=0.3f)),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = "Bookmark", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(6.dp))
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Transparent,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=0.3f)),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(6.dp))
                                }
                            }

                            // Times Count
                            Text(
                                text = "$targetCount مرات",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Interactive Counter Selection (Footer)
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    // Left Arrow
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondary, // Gold color
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(40.dp)
                    ) {
                        IconButton(onClick = { /* Previous Dhikr */ }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous", tint = Color.White)
                        }
                    }
                    
                    // Main Counter Button & Info
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Big Clickable Circle
                        Surface(
                            shape = CircleShape,
                            color = GreenPrimaryLight,
                            shadowElevation = 8.dp,
                            modifier = Modifier.size(100.dp),
                            onClick = { 
                                if (currentCount < targetCount) currentCount++ 
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                // Background ring (lighter green)
                                CircularProgressIndicator(
                                    progress = { 1f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = GreenPrimaryLight.copy(alpha = 0.3f),
                                    strokeWidth = 6.dp
                                )
                                // Active progress ring
                                CircularProgressIndicator(
                                    progress = { currentCount.toFloat() / targetCount.toFloat() },
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color.White,
                                    strokeWidth = 6.dp
                                )
                                
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                     // Finger Touch Icon
                                     Icon(
                                         imageVector = Icons.Default.TouchApp,
                                         contentDescription = "Tap",
                                         tint = Color.White,
                                         modifier = Modifier.size(28.dp).padding(bottom = 4.dp)
                                     )
                                     Text(
                                         text = "$currentCount/$targetCount",
                                         style = MaterialTheme.typography.titleMedium,
                                         fontWeight = FontWeight.Bold,
                                         color = Color.White
                                     )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dhikr Index
                        Text(
                            text = "1 / 21",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        // Dhikr Title
                        Text(
                            text = dhikrTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Right Arrow
                    Surface(
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, Color(0xFFC9A24D)), // Gold outline for inactive/next
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(40.dp)
                    ) {
                        IconButton(onClick = { /* Next Dhikr */ }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color(0xFFC9A24D))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
