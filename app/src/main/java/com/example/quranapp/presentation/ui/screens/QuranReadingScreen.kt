package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quranapp.presentation.viewmodel.SurahDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReadingScreen(
    navController: NavController,
    surahId: Int = 1,
    viewModel: SurahDetailViewModel = hiltViewModel()
) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()
    var sliderPosition by remember { mutableStateOf(0f) }
    var isAudioPlayerOpen by remember { mutableStateOf(false) }

    val surah by viewModel.surah.collectAsState()
    val ayahs by viewModel.ayahs.collectAsState()

    LaunchedEffect(surahId) {
        viewModel.loadSurah(surahId)
    }

    val surahNameToDisplay = surah?.nameArabic ?: "جاري التحميل..."

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            QuranReadingFooter(
                surahNameToDisplay = surahNameToDisplay,
                sliderPosition = sliderPosition,
                onSliderValueChange = { sliderPosition = it }
            )
            if (isAudioPlayerOpen) {
                QuranAudioPlayerOverlay(onClose = { isAudioPlayerOpen = false })
            }
        }
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
                // Action Buttons (Left)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Audio
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAudioPlayerOpen) MaterialTheme.colorScheme.surface else GreenPrimaryLight,
                        border = if (isAudioPlayerOpen) BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)) else null,
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = { isAudioPlayerOpen = !isAudioPlayerOpen }) {
                            Icon(
                                imageVector = if (isAudioPlayerOpen) Icons.Default.Close else Icons.Default.Headphones,
                                contentDescription = if (isAudioPlayerOpen) "Close Audio" else "Listen",
                                tint = if (isAudioPlayerOpen) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    // Bookmark
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.5f)),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = "Bookmark", tint = GreenPrimaryLight)
                        }
                    }
                    // Share
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.5f)),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = GreenPrimaryLight)
                        }
                    }
                }
                
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

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = spacing.gridMargin)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Bismillah Banner (Placeholder for decorative image)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Divider(color = GreenPrimaryLight, thickness = 2.dp, modifier = Modifier.padding(horizontal = 32.dp))
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        style = MaterialTheme.typography.headlineMedium,
                        color = GreenPrimaryLight,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    Divider(color = GreenPrimaryLight, thickness = 2.dp, modifier = Modifier.padding(horizontal = 32.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Quranic Text Example
                if (ayahs.isEmpty()) {
                     CircularProgressIndicator(
                         modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally),
                         color = GreenPrimaryLight
                     )
                } else {
                    val fullText = ayahs.joinToString(" ") { "${it.text} ﴿${it.numberInSurah}﴾" }
                    Text(
                        text = fullText,
                        style = MaterialTheme.typography.headlineLarge, // Ideally use Scheherazade New
                        fontSize = 28.sp,
                        lineHeight = 44.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Page Number Indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GreenPrimaryLight.copy(alpha = 0.1f),
                        modifier = Modifier.size(36.dp),
                        border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.3f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "1",
                                style = MaterialTheme.typography.titleMedium,
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

@Composable
fun QuranReadingFooter(
    surahNameToDisplay: String,
    sliderPosition: Float,
    onSliderValueChange: (Float) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dropdown Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "سُورَةُ $surahNameToDisplay",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Slider & Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Up Arrow
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Previous",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(6.dp)
                    )
                }

                // Slider
                Slider(
                    value = sliderPosition,
                    onValueChange = onSliderValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                )

                // Down Arrow
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp)) // Safe area bottom padding
        }
    }
}

@Composable
fun QuranAudioPlayerOverlay(onClose: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dropdown Indicator for Audio
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onClose() }
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "10:00",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = { 0.1f },
                    color = GreenPrimaryLight,
                    trackColor = GreenPrimaryLight.copy(alpha = 0.2f),
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "1:50:00",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(imageVector = Icons.Default.Repeat, contentDescription = "Loop", tint = MaterialTheme.colorScheme.onSurface)
                Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Previous", tint = MaterialTheme.colorScheme.onSurface)
                
                Surface(
                    shape = CircleShape,
                    color = GreenPrimaryLight,
                    modifier = Modifier.size(56.dp)
                ) {
                    IconButton(onClick = { /* TODO Play/Pause */ }) {
                        Icon(imageVector = Icons.Default.Pause, contentDescription = "Pause", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
                
                Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next", tint = MaterialTheme.colorScheme.onSurface)
                Icon(imageVector = Icons.Default.HeadsetMic, contentDescription = "Audio Settings", tint = GreenPrimaryLight)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Reciter Selection Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language Toggle (Placeholder)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Row(modifier = Modifier.padding(2.dp)) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "العربية",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        Text(
                            text = "الإنجليزية",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
                
                Text(
                    text = "إختر القارئ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reciters List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ReciterCard(name = "عبدالباسط عبد الصمد", isSelected = true, modifier = Modifier.weight(1f))
                ReciterCard(name = "ماهر المعيقلي", isSelected = false, modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ReciterCard(name: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.DarkGray, // Placeholder for image background
        border = if (isSelected) BorderStroke(2.dp, GreenPrimaryLight) else null,
        modifier = modifier.aspectRatio(1f) // Square
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Placeholder Image overlay
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
            
            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = GreenPrimaryLight,
                    modifier = Modifier.padding(8.dp).align(Alignment.TopStart).size(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.padding(4.dp))
                }
            } else {
                 Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, Color.White),
                    modifier = Modifier.padding(8.dp).align(Alignment.TopStart).size(24.dp)
                ) {}
            }

            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }
}
