package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.theme.spacing
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quranapp.presentation.viewmodel.SurahListViewModel
import com.example.quranapp.domain.model.Surah
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranIndexScreen(
    navController: NavController,
    viewModel: SurahListViewModel = hiltViewModel()
) {
    val spacing = MaterialTheme.spacing
    var selectedTab by remember { mutableStateOf(0) } // 0 for Surah, 1 for Juz

    val surahs by viewModel.surahs.collectAsState()
    val juzBoundaries by viewModel.juzBoundaries.collectAsState()
    val surahStartPages by viewModel.surahStartPages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSurahs()
    }

    // Map Ayah boundaries into logical Juz structures for UI
    val juzs = remember(juzBoundaries, surahs) {
        if (surahs.isEmpty() || juzBoundaries.isEmpty()) emptyList()
        else {
            juzBoundaries.mapIndexed { index, ayah ->
                val startSurahNumber = ayah.surahNumber
                val endSurahNumber = if (index < juzBoundaries.size - 1) {
                    juzBoundaries[index + 1].surahNumber
                } else {
                    114
                }
                
                // Get all surahs that this Juz passes through
                val juzSurahs = surahs.filter { it.number in startSurahNumber..endSurahNumber }
                
                val verses = juzSurahs.mapNotNull { surah ->
                    val textDesc = when {
                        surah.number == startSurahNumber && surah.number == endSurahNumber -> {
                            val endAyah = if (index < juzBoundaries.size - 1) juzBoundaries[index + 1].numberInSurah - 1 else surah.numberOfAyahs
                            if (ayah.numberInSurah == 1 && endAyah == surah.numberOfAyahs) "السورة كاملة"
                            else "الآيات ${ayah.numberInSurah} - $endAyah"
                        }
                        surah.number == startSurahNumber -> {
                            if (ayah.numberInSurah == 1) "السورة كاملة"
                            else "من آية ${ayah.numberInSurah} إلى آخر السورة"
                        }
                        surah.number == endSurahNumber -> {
                            val endAyah = if (index < juzBoundaries.size - 1) juzBoundaries[index + 1].numberInSurah - 1 else surah.numberOfAyahs
                            if (endAyah == surah.numberOfAyahs) "السورة كاملة"
                            else if (endAyah > 0) "من بداية السورة إلى آية $endAyah"
                            else null
                        }
                        else -> "السورة كاملة"
                    }
                    
                    if (textDesc == null) null
                    else {
                        QuranJuzVerseData(
                            text = textDesc,
                            details = "سُورَةُ ${surah.nameArabic}",
                            badge = surah.number.toString(), // We can use Surah number as the badge instead of Page since Page is 0 right now
                            surahNumber = surah.number
                        )
                    }
                }
                
                QuranJuzData(
                    id = ayah.juz,
                    name = "الجزء ${ayah.juz}",
                    verses = verses
                )
            }
        }
    }



    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    // Sync tab with pager swipe
    LaunchedEffect(pagerState.currentPage) {
        selectedTab = pagerState.currentPage
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
                // Back Button (Right in Arabic)
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

                Text(
                    text = "القرآن الكريم",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Search Button (Left in Arabic)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GreenPrimaryLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tab Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gridMargin),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        // السورة Tab (First in code = Right in RTL)
                        QuranTabItem(
                            title = "السورة",
                            count = "114",
                            isSelected = selectedTab == 0,
                            onClick = { 
                                selectedTab = 0 
                                coroutineScope.launch { pagerState.animateScrollToPage(0) }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        // الجزء Tab (Middle)
                        QuranTabItem(
                            title = "الأجزاء",
                            count = "30",
                            isSelected = selectedTab == 1,
                            onClick = { 
                                selectedTab = 1 
                                coroutineScope.launch { pagerState.animateScrollToPage(1) }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        // العلامات Tab (Last in code = Left in RTL)
                        QuranTabItem(
                            title = "العلامات",
                            count = "0",
                            isSelected = selectedTab == 2,
                            onClick = { 
                                selectedTab = 2 
                                coroutineScope.launch { pagerState.animateScrollToPage(2) }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Swipeable Content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true
            ) { page ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.gridMargin),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (page) {
                        0 -> {
                            // Surah View
                            if (isLoading) {
                                item {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentWidth(Alignment.CenterHorizontally),
                                        color = GreenPrimaryLight
                                    )
                                }
                            } else if (errorMessage != null) {
                                item {
                                    Text(
                                        "Error: $errorMessage",
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            } else if (surahs.isEmpty()) {
                                item {
                                    Text(
                                        "جاري تحميل البيانات، يرجى التأكد من اتصالك بالإنترنت...",
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                                    )
                                }
                            } else {
                                items(surahs.size) { index ->
                                    val surah = surahs[index]
                                    val startPage = surahStartPages[surah.number] ?: 1
                                    QuranSurahItem(
                                        surah = surah,
                                        startPage = startPage,
                                        navController = navController
                                    )
                                    if (index < surahs.size - 1) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Juz View
                            if (isLoading) {
                                item {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentWidth(Alignment.CenterHorizontally),
                                        color = GreenPrimaryLight
                                    )
                                }
                            } else if (errorMessage != null) {
                                item {
                                    Text(
                                        "Error: $errorMessage",
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            } else if (juzs.isEmpty()) {
                                 item {
                                    Text(
                                        "جاري تحميل البيانات...",
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                                    )
                                }
                            } else {
                                items(juzs) { juz ->
                                    QuranJuzAccordion(juz = juz, navController = navController)
                                }
                            }
                        }
                        2 -> {
                            // Bookmarks View
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 64.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFC9A24D).copy(alpha = 0.1f),
                                        modifier = Modifier.size(80.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Bookmarks",
                                                tint = Color(0xFFC9A24D),
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = "لا توجد علامات حتى الآن",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "أضف علامة أثناء القراءة لتسهيل العودة إليها لاحقاً",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
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

@Composable
fun QuranTabItem(title: String, count: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val backgroundColor = if (isSelected) GreenPrimaryLight.copy(alpha = 0.1f) else Color.Transparent
    val contentColor = if (isSelected) GreenPrimaryLight else MaterialTheme.colorScheme.onSurface
    
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) GreenPrimaryLight.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            ) {
                Text(
                    text = count,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
fun QuranSurahItem(surah: Surah, startPage: Int, navController: NavController) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp, // Soft elegant shadow
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.QuranReading.createRoute(startPage)) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Right Side (Surah Info & Icon) - Starts on Right in RTL
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Diagonal elegant Box with number
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Soft background circle
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFC9A24D).copy(alpha = 0.15f), // Soft gold background
                        modifier = Modifier.fillMaxSize()
                    ) {}
                    // Inner rotated diamond or secondary circle
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, Color(0xFFC9A24D).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxSize(0.7f)
                    ) {}
                    
                    Text(
                        text = surah.number.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "سُورَةُ ${surah.nameArabic}",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = com.example.quranapp.presentation.ui.theme.ScheherazadeNew,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimaryLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (surah.revelationType == "Meccan") "مَكِّيَّة" else "مَدَنِيَّة",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .size(4.dp)
                                .background(Color(0xFFC9A24D), CircleShape)
                        )
                        Text(
                            text = "${surah.numberOfAyahs} آيات",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Left Side (English Name or Arrow)
            Column(horizontalAlignment = Alignment.End) {
                 Text(
                    text = surah.nameEnglish,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = GreenPrimaryLight.copy(alpha = 0.7f)
                )
                 Text(
                    text = surah.englishNameTranslation,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun QuranJuzAccordion(juz: QuranJuzData, navController: NavController) {
    var expanded by remember { mutableStateOf(juz.id == 1) }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (expanded) 6.dp else 2.dp, // Dynamic elevation
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Juz Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Right Side: Number & Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GreenPrimaryLight.copy(alpha = 0.08f),
                            modifier = Modifier.fillMaxSize()
                        ) {}
                        Text(
                            text = juz.id.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimaryLight
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = juz.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = com.example.quranapp.presentation.ui.theme.ScheherazadeNew,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Expand/Collapse Icon
                Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand/Collapse",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(8.dp).size(20.dp)
                    )
                }
            }

            // Expanded List
            if (expanded) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )
                    juz.verses.forEachIndexed { index, verse ->
                        QuranJuzVerseItem(verse = verse, navController = navController)
                        if (index < juz.verses.size - 1) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f),
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuranJuzVerseItem(verse: QuranJuzVerseData, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.QuranReading.createRoute(verse.surahNumber)) }
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFC9A24D).copy(alpha = 0.1f),
                border = BorderStroke(1.dp, Color(0xFFC9A24D).copy(alpha = 0.2f))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "صـ",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFC9A24D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                    Text(
                        text = verse.badge,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFC9A24D),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = verse.details,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = com.example.quranapp.presentation.ui.theme.ScheherazadeNew,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimaryLight
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = verse.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }
        }
        
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Read",
            tint = Color(0xFFC9A24D),
            modifier = Modifier.size(20.dp)
        )
    }
}

data class QuranJuzData(val id: Int, val name: String, val verses: List<QuranJuzVerseData>)
data class QuranJuzVerseData(val text: String, val details: String, val badge: String, val surahNumber: Int)
