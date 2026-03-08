package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.presentation.ui.theme.spacing
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quranapp.presentation.viewmodel.TafsirDetailViewModel
import androidx.compose.material.icons.filled.Bookmark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TafsirDetailScreen(
    navController: NavController,
    surahName: String = "سُورَةُ الْفَاتِحَةِ",
    surahNumber: Int = 1,
    viewModel: TafsirDetailViewModel = hiltViewModel()
) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()
    
    val isBookmarked by viewModel.isBookmarked.collectAsState()
    val tafsirTextList by viewModel.tafsirTextList.collectAsState()
    val isLoading by viewModel.isLoadingTafsir.collectAsState()
    val errorMessage by viewModel.tafsirError.collectAsState()

    LaunchedEffect(surahNumber) {
        viewModel.checkIfBookmarked(surahNumber)
        viewModel.loadTafsir(surahNumber)
    }

    val tabs = listOf(
        "تفسير الجلالين",
        "تفسير الميسر"
    )
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(selectedTabIndex) {
        val edition = if (selectedTabIndex == 0) "ar.jalalayn" else "ar.muyassar"
        viewModel.loadTafsir(surahNumber, edition)
    }

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
                // Action Buttons (Left in LTR, Right visually if RTL mirror applied, wait, design has them on Left)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    // Bookmark
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isBookmarked) GreenPrimaryLight else MaterialTheme.colorScheme.surface,
                        border = if (isBookmarked) null else BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.5f)),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = { viewModel.toggleBookmark(surahName) }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.surface else GreenPrimaryLight
                            )
                        }
                    }
                    // Share
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.5f)),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = { 
                            val shareText = "اقرأ تفسير سورة $surahName عبر تطبيق زاد مسلم"
                            val sendIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(android.content.Intent.createChooser(sendIntent, "مشاركة التفسير"))
                        }) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = GreenPrimaryLight)
                        }
                    }
                }
                
                Text(
                    text = surahName,
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

            // Scrollable Tabs
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = spacing.gridMargin),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(tabs.indices.toList()) { index ->
                    TafsirTabItem(
                        title = tabs[index],
                        isSelected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Scrollable Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = spacing.gridMargin, vertical = 8.dp)
            ) {
                // Ayah Text Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Bismillah (Green)
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            style = MaterialTheme.typography.titleLarge,
                            color = GreenPrimaryLight,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Quranic Text (Placeholder for Al-Fatihah)
                        Text(
                            text = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ﴿١﴾ الرَّحْمَٰنِ الرَّحِيمِ ﴿٢﴾ مَالِكِ يَوْمِ الدِّينِ ﴿٣﴾ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ﴿٤﴾ اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ﴿٥﴾ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ ﴿٦﴾",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp,
                            lineHeight = 32.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tafsir Content Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tabs[selectedTabIndex],
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook, // Placeholder for the open-book golden icon
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic Content based on selected tab
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
                } else if (tafsirTextList.isEmpty()) {
                    Text(
                        text = "لا يتوفر تفسير حالياً",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    tafsirTextList.forEach { tafsir ->
                        AyahTafsirItem(ayahText = "الآية ${tafsir.ayahNumber}", tafsirText = tafsir.text)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AyahTafsirItem(ayahText: String, tafsirText: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        // Ayah Box (Green tint)
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = GreenPrimaryLight.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.3f)),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(
                text = ayahText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        // Tafsir Text
        Text(
            text = tafsirText,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 28.sp,
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun TafsirTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface 
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    val borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            fontWeight = fontWeight,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
