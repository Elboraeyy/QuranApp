package com.example.quranapp.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.quranapp.presentation.ui.theme.quranic
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults

fun Number.toArabicNumerals(): String {
    val arabicNumerals = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return this.toString().map { char ->
        if (char.isDigit()) arabicNumerals[char.toString().toInt()] else char
    }.joinToString("")
}

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
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

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
            AnimatedVisibility(
                visible = isAudioPlayerOpen,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                if (isAudioPlayerOpen) {
                    QuranAudioPlayerOverlay(
                        surahNumber = surahId, // Changed from surahNumber to surahId
                        onClose = { isAudioPlayerOpen = false }
                    )
                }
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
                // Back Button (Right in design/Arabic)
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Standard Back icon
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Action Buttons (Left)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    // Share
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFC9A24D).copy(alpha = 0.1f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        IconButton(onClick = { 
                            val shareText = "استمع واقرأ سورة ${surah?.nameArabic ?: ""} عبر تطبيق زاد مسلم"
                            val sendIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(android.content.Intent.createChooser(sendIntent, "مشاركة السورة"))
                        }) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = GreenPrimaryLight)
                        }
                    }
                    // Bookmark
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFC9A24D).copy(alpha = 0.1f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        val isBookmarked by viewModel.isBookmarked.collectAsState()
                        IconButton(onClick = { viewModel.toggleBookmark() }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = GreenPrimaryLight
                            )
                        }
                    }
                    // Audio
                    Surface(
                        shape = CircleShape,
                        color = if (isAudioPlayerOpen) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else Color(0xFFC9A24D).copy(alpha = 0.1f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        IconButton(onClick = { isAudioPlayerOpen = !isAudioPlayerOpen }) {
                            Icon(
                                imageVector = if (isAudioPlayerOpen) Icons.Default.Close else Icons.Default.Headphones,
                                contentDescription = if (isAudioPlayerOpen) "Close Audio" else "Listen",
                                tint = if (isAudioPlayerOpen) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
                            )
                        }
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

                // Bismillah Banner
                if (surahId != 1 && surahId != 9) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HorizontalDivider(
                            color = Color(0xFFC9A24D).copy(alpha = 0.4f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 64.dp)
                        )
                        Text(
                            text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", // Exact Uthmani chars
                            style = MaterialTheme.typography.headlineMedium.quranic,
                            color = GreenPrimaryLight,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp),
                            fontSize = 32.sp
                        )
                        HorizontalDivider(
                            color = Color(0xFFC9A24D).copy(alpha = 0.4f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 64.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Quranic Text Example
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        color = GreenPrimaryLight
                    )
                } else if (errorMessage != null) {
                    Text(
                        text = "Error: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else if (ayahs.isNotEmpty()) {
                    val fullText = ayahs.mapIndexed { index, ayah ->
                        var text = ayah.text
                        // Strip bismillah if it's the first ayah and not Surah 1
                        if (index == 0 && surahId != 1 && surahId != 9) {
                            // AlQuran Cloud Uthmani text exact prefix
                            val bismillahPrefix = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"
                            if (text.startsWith(bismillahPrefix)) {
                                text = text.removePrefix(bismillahPrefix).trim()
                            }
                        }
                        "$text \uFD3F${ayah.numberInSurah.toArabicNumerals()}\uFD3E"
                    }.joinToString(" ")
                    Text(
                        text = fullText,
                        style = MaterialTheme.typography.headlineLarge.quranic,
                        fontSize = 30.sp,
                        lineHeight = 52.sp,
                        textAlign = TextAlign.Justify,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                } else {
                    Text(
                        text = "لا توجد بيانات متاحة حالياً",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
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
                        color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp),
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
        shadowElevation = 16.dp, // Premium elevation
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
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
                    color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Previous",
                        tint = GreenPrimaryLight,
                        modifier = Modifier.padding(8.dp)
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
                    color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Next",
                        tint = GreenPrimaryLight,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp)) // Safe area bottom padding
        }
    }
}

@Composable
fun QuranAudioPlayerOverlay(
    surahNumber: Int,
    onClose: () -> Unit,
    viewModel: com.example.quranapp.presentation.viewmodel.AudioPlayerViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

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
                    text = formatTime(currentPosition),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                
                val progressValue = if (duration > 0) (currentPosition.toFloat() / duration.toFloat()) else 0f
                Slider(
                    value = progressValue,
                    onValueChange = { newVal ->
                        if (duration > 0) {
                            viewModel.seekTo((newVal * duration).toLong())
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = GreenPrimaryLight,
                        activeTrackColor = GreenPrimaryLight,
                        inactiveTrackColor = GreenPrimaryLight.copy(alpha = 0.2f)
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatTime(duration),
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
                
                IconButton(onClick = { viewModel.previous() }) {
                    Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Previous", tint = MaterialTheme.colorScheme.onSurface)
                }
                
                Surface(
                    shape = CircleShape,
                    color = GreenPrimaryLight,
                    modifier = Modifier.size(56.dp)
                ) {
                    IconButton(onClick = { 
                        if (isPlaying) {
                            viewModel.pause()
                        } else {
                            if (currentPosition > 0L) {
                                viewModel.resume()
                            } else {
                                viewModel.playSurah(surahNumber)
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                IconButton(onClick = { viewModel.next() }) {
                    Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next", tint = MaterialTheme.colorScheme.onSurface)
                }
                
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
