package com.example.quranapp.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.platform.LocalContext
import android.graphics.Typeface
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.Typeface as ComposeTypeface
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.presentation.ui.theme.spacing
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quranapp.presentation.viewmodel.SurahDetailViewModel
import com.example.quranapp.presentation.viewmodel.AudioPlayerViewModel
import com.example.quranapp.presentation.ui.theme.ScheherazadeNew
import com.example.quranapp.presentation.ui.components.AyahQuickView
import com.example.quranapp.domain.model.Ayah
import com.example.quranapp.domain.model.QcfVerse
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import com.example.quranapp.util.ImageShareUtil
import androidx.compose.ui.graphics.asAndroidBitmap

fun Number.toArabicNumerals(): String {
    val arabicNumerals = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return this.toString().map { char ->
        if (char.isDigit()) arabicNumerals[char.toString().toInt()] else char
    }.joinToString("")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun QuranReadingScreen(
    navController: NavController,
    startPage: Int = 1,
    viewModel: SurahDetailViewModel = hiltViewModel(),
    audioViewModel: AudioPlayerViewModel = hiltViewModel()
) {
    val surah by viewModel.surah.collectAsState()
    val qcfPage by viewModel.qcfPage.collectAsState()
    val isBookmarked by viewModel.isBookmarked.collectAsState()
    val spacing = MaterialTheme.spacing
    var showOverlay by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    val errorMessage by viewModel.errorMessage.collectAsState()

    val pagerState = rememberPagerState(initialPage = startPage - 1, pageCount = { 604 })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val captureController = rememberCaptureController()

    // Audio states
    val isPlaying by audioViewModel.isPlaying.collectAsState()
    val currentAyahIndex by audioViewModel.currentAyahIndex.collectAsState()
    val isPageMode by audioViewModel.isPageMode.collectAsState()
    val currentPageAyahs by audioViewModel.currentPageAyahs.collectAsState()
    val selectedReciterName by audioViewModel.selectedReciterName.collectAsState()

    // Quick View states
    var selectedAyah by remember { mutableStateOf<Ayah?>(null) }
    var selectedAyahSurahName by remember { mutableStateOf("") }
    var tafsirText by remember { mutableStateOf<String?>(null) }
    var meaningsText by remember { mutableStateOf<String?>(null) }
    var showQuickView by remember { mutableStateOf(false) }
    var showReciterPicker by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.loadPage(pagerState.currentPage + 1)
    }

    // Auto-page-turn when page audio completes
    LaunchedEffect(Unit) {
        audioViewModel.pageCompleted.collect {
            val nextPage = pagerState.currentPage + 1
            if (nextPage < 604) {
                pagerState.animateScrollToPage(nextPage)
                // After page loads, start playing the new page
                kotlinx.coroutines.delay(800)
                viewModel.qcfPage.value?.let { page ->
                    audioViewModel.playPage(page.verses)
                }
            }
        }
    }

    val surahNameToDisplay = surah?.nameArabic ?: ""

    val surahNames = remember {
        listOf(
            "الفاتحة","البقرة","آل عمران","النساء","المائدة","الأنعام","الأعراف","الأنفال","التوبة","يونس",
            "هود","يوسف","الرعد","إبراهيم","الحجر","النحل","الإسراء","الكهف","مريم","طه",
            "الأنبياء","الحج","المؤمنون","النور","الفرقان","الشعراء","النمل","القصص","العنكبوت","الروم",
            "لقمان","السجدة","الأحزاب","سبأ","فاطر","يس","الصافات","ص","الزمر","غافر",
            "فصلت","الشورى","الزخرف","الدخان","الجاثية","الأحقاف","محمد","الفتح","الحجرات","ق",
            "الذاريات","الطور","النجم","القمر","الرحمن","الواقعة","الحديد","المجادلة","الحشر","الممتحنة",
            "الصف","الجمعة","المنافقون","التغابن","الطلاق","التحريم","الملك","القلم","الحاقة","المعارج",
            "نوح","الجن","المزمل","المدثر","القيامة","الإنسان","المرسلات","النبأ","النازعات","عبس",
            "التكوير","الانفطار","المطففين","الانشقاق","البروج","الطارق","الأعلى","الغاشية","الفجر","البلد",
            "الشمس","الليل","الضحى","الشرح","التين","العلق","القدر","البينة","الزلزلة","العاديات",
            "القارعة","التكاثر","العصر","الهمزة","الفيل","قريش","الماعون","الكوثر","الكافرون","النصر",
            "المسد","الإخلاص","الفلق","الناس"
        )
    }

    val juzStartPages = remember {
        listOf(1,22,42,62,82,102,122,142,162,182,202,222,242,262,282,302,322,332,342,362,382,402,422,442,462,482,502,522,542,562,582,604)
    }

    val surahPages = remember {
        listOf(
            1,2,50,77,106,128,151,177,187,208,221,235,249,255,262,267,282,293,305,312,
            322,332,342,350,359,367,377,385,396,404,411,415,418,428,434,440,446,453,458,467,
            477,483,489,496,499,502,507,511,515,518,520,523,526,528,531,534,537,542,545,549,
            551,553,554,556,558,560,562,564,566,568,570,572,574,575,577,578,580,582,583,585,
            586,587,587,589,590,591,591,592,593,594,595,595,596,596,597,597,598,598,599,599,
            600,600,601,601,601,602,602,602,603,603,603,604,604,604
        )
    }

    val currentPage = pagerState.currentPage + 1
    val currentJuz = remember(currentPage) {
        juzStartPages.indexOfLast { currentPage >= it } + 1
    }

    var showSurahPicker by remember { mutableStateOf(false) }
    var showJuzPicker by remember { mutableStateOf(false) }
    var showPagePicker by remember { mutableStateOf(false) }

    // Map verses to help identify which ayah a line belongs to
    val pageVerses = qcfPage?.verses ?: emptyList()

    data class AyahPlacement(
        val verse: QcfVerse,
        val startIndex: Int,
        val endIndex: Int
    )

    // Build a mapping: for each QCF line that is NOT a header, figure out

    val lineToAyahsMap = remember(qcfPage) {
        val map = mutableMapOf<Int, List<AyahPlacement>>()
        val textLines = qcfPage?.lines?.filter { it.surahNumber == null && !it.isBismillah }?.sortedBy { it.lineNumber } ?: emptyList()
        val verses = qcfPage?.verses?.sortedBy { it.id } ?: emptyList()
        
        if (textLines.isNotEmpty() && verses.isNotEmpty()) {
            var currentVerseIndex = 0
            var verseCharIndex = 0
            var currentVerseCode = verses.getOrNull(0)?.codeV1?.replace(" ", "") ?: ""
            
            for (line in textLines) {
                val placements = mutableListOf<AyahPlacement>()
                val originalText = line.text
                var lineCharIndex = 0
                var currentPlacementStart = 0
                
                while (lineCharIndex < originalText.length && currentVerseIndex < verses.size) {
                    val char = originalText[lineCharIndex]
                    if (char == ' ' || char == '\n' || char == '\r') {
                        if (currentPlacementStart == lineCharIndex) {
                            currentPlacementStart++
                        }
                        lineCharIndex++
                        continue
                    }
                    
                    val currentVerse = verses[currentVerseIndex]
                    verseCharIndex++
                    lineCharIndex++
                    
                    if (verseCharIndex >= currentVerseCode.length) {
                        placements.add(AyahPlacement(currentVerse, currentPlacementStart, lineCharIndex))
                        currentVerseIndex++
                        if (currentVerseIndex < verses.size) {
                            currentVerseCode = verses[currentVerseIndex].codeV1.replace(" ", "")
                            verseCharIndex = 0
                            currentPlacementStart = lineCharIndex
                        }
                    }
                }
                
                if (currentPlacementStart < originalText.length && currentVerseIndex < verses.size) {
                    placements.add(AyahPlacement(verses[currentVerseIndex], currentPlacementStart, originalText.length))
                }
                
                map[line.lineNumber] = placements
            }
        }
        map
    }

    // Full-screen layout
    Capturable(
        controller = captureController,
        onCaptured = { bitmap, error ->
            if (bitmap != null) {
                ImageShareUtil.shareBitmap(context, bitmap.asAndroidBitmap())
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { showOverlay = !showOverlay }
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // SURAH / JUZ HEADER
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showSurahPicker = true }
                    ) {
                        Text(
                            text = surahNameToDisplay,
                            style = MaterialTheme.typography.labelLarge,
                            color = GreenPrimaryLight,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showJuzPicker = true }
                    ) {
                        Text(
                            text = "الجزء ${currentJuz.toArabicNumerals()}",
                            style = MaterialTheme.typography.labelLarge,
                            color = GreenPrimaryLight,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // QURAN PAGER
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { pageIndex ->
                    val actualPage = pageIndex + 1

                    val fontPath = String.format("fonts/QCF_P%03d.TTF", actualPage)
                    val typeface = remember(actualPage) {
                        try {
                            Typeface.createFromAsset(context.assets, fontPath)
                        } catch (e: Exception) {
                            Typeface.DEFAULT
                        }
                    }
                    val qcfFontFamily = remember(typeface) { FontFamily(ComposeTypeface(typeface)) }

                    if (qcfPage?.pageNumber == actualPage && qcfPage?.lines?.isNotEmpty() == true) {
                        BoxWithConstraints(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val availableWidth = maxWidth
                            val fontSize = (availableWidth.value / 16f).sp
                            val lineHeight = fontSize * 1.8f

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 64.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {
                                qcfPage?.lines?.forEach { line ->
                                    if (line.isBismillah) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 0.dp, bottom = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                                fontFamily = ScheherazadeNew,
                                                fontSize = 22.sp,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                    } else if (line.surahNumber != null && line.surahNumber!! > 0) {
                                        val surahIdx = line.surahNumber!! - 1
                                        val sName = if (surahIdx in surahNames.indices) surahNames[surahIdx] else ""
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp, bottom = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth(0.85f).height(45.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data("file:///android_asset/surah_frame.svg")
                                                        .decoderFactory(SvgDecoder.Factory())
                                                        .build(),
                                                    contentDescription = "Surah Frame",
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = androidx.compose.ui.layout.ContentScale.FillBounds,
                                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                                                )
                                                Text(
                                                    text = "سُورَةُ $sName",
                                                    fontFamily = ScheherazadeNew,
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onBackground,
                                                    modifier = Modifier.padding(bottom = 6.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        // Determine if this line contains the currently playing ayah
                                        val placementsOnLine = lineToAyahsMap[line.lineNumber] ?: emptyList()
                                        val playingAyah = if (isPageMode && currentAyahIndex >= 0) currentPageAyahs.getOrNull(currentAyahIndex) else null

                                        val annotatedString = androidx.compose.ui.text.buildAnnotatedString {
                                            append(line.text)
                                            if (playingAyah != null) {
                                                for (placement in placementsOnLine) {
                                                    if (placement.verse.surahNumber == playingAyah.surahNumber &&
                                                        placement.verse.ayahNumber == playingAyah.ayahNumber) {
                                                        
                                                        addStyle(
                                                            style = androidx.compose.ui.text.SpanStyle(
                                                                background = Color(0xFFC9A24D).copy(alpha = 0.25f),
                                                                color = GreenPrimaryLight
                                                            ),
                                                            start = placement.startIndex.coerceAtLeast(0),
                                                            end = placement.endIndex.coerceAtMost(line.text.length)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Text(
                                            text = annotatedString,
                                            fontFamily = qcfFontFamily,
                                            fontSize = fontSize,
                                            lineHeight = lineHeight,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            maxLines = 1,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .pointerInput(Unit) {
                                                    detectTapGestures(
                                                        onTap = { showOverlay = !showOverlay },
                                                        onLongPress = { offset ->
                                                            // Long press — identify the ayah and show quick view
                                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            val placement = placementsOnLine.firstOrNull()
                                                            placement?.verse?.let { verse ->
                                                                scope.launch {
                                                                    tafsirText = null
                                                                    meaningsText = null
                                                                    val ayahData = viewModel.getAyahForQuickView(
                                                                        verse.surahNumber,
                                                                        verse.ayahNumber
                                                                    )
                                                                    if (ayahData != null) {
                                                                        selectedAyah = ayahData
                                                                        val surahData = viewModel.getSurahName(verse.surahNumber)
                                                                        selectedAyahSurahName = surahData ?: ""
                                                                        
                                                                        // Show modal immediately, then fetch tafsir/meanings
                                                                        showQuickView = true
                                                                        
                                                                        // Fetch Tafsir (Jalalayn)
                                                                        tafsirText = viewModel.getTafsirText(verse.surahNumber, verse.ayahNumber, "ar.jalalayn")
                                                                        // Fetch Meanings (Muyassar)
                                                                        meaningsText = viewModel.getTafsirText(verse.surahNumber, verse.ayahNumber, "ar.muyassar")
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    )
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = GreenPrimaryLight)
                        }
                    }
                }
            }
        }

        // PAGE NUMBER — bottom center
        if (!isPageMode || !isPlaying) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { showPagePicker = true },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = (pagerState.currentPage + 1).toArabicNumerals(),
                            style = MaterialTheme.typography.titleMedium,
                            color = GreenPrimaryLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // TOP BAR OVERLAY
        AnimatedVisibility(
            visible = showOverlay,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.gridMargin, vertical = 8.dp)
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = surahNameToDisplay,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Share
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFC9A24D).copy(alpha = 0.1f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = {
                                showOverlay = false
                                scope.launch {
                                    kotlinx.coroutines.delay(350) // wait for exit animation
                                    captureController.capture()
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = GreenPrimaryLight)
                            }
                        }
                        // Bookmark
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFC9A24D).copy(alpha = 0.1f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = { viewModel.toggleBookmark() }) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = GreenPrimaryLight
                                )
                            }
                        }
                        // Audio Play
                        Surface(
                            shape = CircleShape,
                            color = if (isPageMode && isPlaying) GreenPrimaryLight.copy(alpha = 0.2f) else Color(0xFFC9A24D).copy(alpha = 0.1f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = {
                                if (isPageMode && isPlaying) {
                                    audioViewModel.stop()
                                } else {
                                    qcfPage?.let { page ->
                                        audioViewModel.playPage(page.verses)
                                    }
                                }
                                showOverlay = false
                            }) {
                                Icon(
                                    imageVector = if (isPageMode && isPlaying) Icons.Default.Stop else Icons.Default.Headphones,
                                    contentDescription = "Audio",
                                    tint = if (isPageMode && isPlaying) GreenPrimaryLight else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }

        // AUDIO CONTROL BAR — appears when page audio is playing
        AnimatedVisibility(
            visible = isPageMode,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Progress info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reciter name (clickable to change)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GreenPrimaryLight.copy(alpha = 0.08f),
                            modifier = Modifier.clickable { showReciterPicker = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = GreenPrimaryLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = selectedReciterName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GreenPrimaryLight,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }

                        // Ayah counter
                        Text(
                            text = if (currentPageAyahs.isNotEmpty()) {
                                "آية ${(currentAyahIndex + 1).coerceAtLeast(1).toArabicNumerals()} من ${currentPageAyahs.size.toArabicNumerals()}"
                            } else "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Controls row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Close
                        IconButton(onClick = { audioViewModel.stop() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Stop",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Previous ayah
                        IconButton(onClick = { audioViewModel.previous() }) {
                            Icon(
                                Icons.Default.SkipPrevious,
                                contentDescription = "Previous",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Play/Pause
                        Surface(
                            shape = CircleShape,
                            color = GreenPrimaryLight,
                            modifier = Modifier.size(48.dp)
                        ) {
                            IconButton(onClick = {
                                if (isPlaying) audioViewModel.pause()
                                else audioViewModel.resume()
                            }) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        // Next ayah
                        IconButton(onClick = { audioViewModel.next() }) {
                            Icon(
                                Icons.Default.SkipNext,
                                contentDescription = "Next",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Repeat (placeholder)
                        IconButton(onClick = { /* TODO: repeat mode */ }) {
                            Icon(
                                Icons.Default.Repeat,
                                contentDescription = "Repeat",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
        }
    }

    // QUICK VIEW BOTTOM SHEET
    if (showQuickView) {
        AyahQuickView(
            ayah = selectedAyah,
            surahName = selectedAyahSurahName,
            tafsirText = tafsirText,
            meaningsText = meaningsText,
            onDismiss = { showQuickView = false },
            onPlayAyah = {
                selectedAyah?.let { ayah ->
                    audioViewModel.playAyah(ayah.surahNumber, ayah.numberInSurah)
                }
            },
            onBookmark = {
                viewModel.toggleBookmark()
            },
            isBookmarked = isBookmarked
        )
    }

    // RECITER PICKER - Bottom Sheet
    if (showReciterPicker) {
        ReciterPickerSheet(
            onDismiss = { showReciterPicker = false },
            onReciterSelected = { reciterId ->
                audioViewModel.changeReciter(reciterId)
                showReciterPicker = false
            }
        )
    }

    // PREMIUM PICKER DIALOGS
    if (showSurahPicker) {
        PremiumSearchDialog(
            title = "اختر السورة",
            onDismiss = { showSurahPicker = false },
            placeholder = "ابحث عن سورة...",
            itemsCount = 114,
            initialScrollIndex = surah?.number?.minus(1) ?: 0,
            filterLogic = { index, query ->
                surahNames[index].contains(query, ignoreCase = true) || "سورة ${surahNames[index]}".contains(query)
            },
            onItemSelected = { index ->
                showSurahPicker = false
                scope.launch { pagerState.scrollToPage(surahPages[index] - 1) }
            },
            itemContent = { index ->
                val surahNum = index + 1
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${surahNum.toArabicNumerals()}. ${surahNames[index]}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (surahNameToDisplay == surahNames[index]) FontWeight.Bold else FontWeight.Normal
                    )
                    Text("ص ${surahPages[index].toArabicNumerals()}", style = MaterialTheme.typography.bodySmall, color = GreenPrimaryLight)
                }
            }
        )
    }

    if (showJuzPicker) {
        PremiumSearchDialog(
            title = "اختر الجزء",
            onDismiss = { showJuzPicker = false },
            placeholder = "ابحث عن جزء (مثال: 30)",
            itemsCount = 30,
            initialScrollIndex = currentJuz - 1,
            filterLogic = { index, query ->
                val num = (index + 1).toString()
                val arabicNum = (index + 1).toArabicNumerals()
                num.contains(query) || arabicNum.contains(query) || "الجزء".contains(query)
            },
            onItemSelected = { index ->
                showJuzPicker = false
                scope.launch { pagerState.scrollToPage(juzStartPages[index] - 1) }
            },
            itemContent = { index ->
                val juzNum = index + 1
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الجزء ${juzNum.toArabicNumerals()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (juzNum == currentJuz) FontWeight.Bold else FontWeight.Normal
                    )
                    Text("ص ${juzStartPages[index].toArabicNumerals()}", style = MaterialTheme.typography.bodySmall, color = GreenPrimaryLight)
                }
            }
        )
    }

    if (showPagePicker) {
        PremiumSearchDialog(
            title = "انتقل لصفحة",
            onDismiss = { showPagePicker = false },
            placeholder = "رقم الصفحة (1 - 604)",
            itemsCount = 604,
            initialScrollIndex = currentPage - 1,
            filterLogic = { index, query ->
                val num = (index + 1).toString()
                val arabicNum = (index + 1).toArabicNumerals()
                num.contains(query) || arabicNum.contains(query)
            },
            onItemSelected = { index ->
                showPagePicker = false
                scope.launch { pagerState.scrollToPage(index) }
            },
            itemContent = { index ->
                val pageNum = index + 1
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الصفحة ${pageNum.toArabicNumerals()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (pageNum == currentPage) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        )
    }
}

// ─── Reciter Picker ─────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciterPickerSheet(
    onDismiss: () -> Unit,
    onReciterSelected: (String) -> Unit
) {
    val reciters = remember {
        listOf(
            "abdul_basit" to "عبد الباسط عبد الصمد",
            "al_hudhaifi" to "صالح الهذيفي",
            "al_minshawi" to "محمد صديق المنشاوي",
            "mishary_alfasy" to "مشاري العفاسي",
            "saad_al_ghamdi" to "سعد الغامدي"
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "اختر القارئ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = GreenPrimaryLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            reciters.forEach { (id, name) ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onReciterSelected(id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GreenPrimaryLight.copy(alpha = 0.1f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = GreenPrimaryLight,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = ScheherazadeNew,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// ─── Premium Search Dialog (reused from before) ─────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumSearchDialog(
    title: String,
    onDismiss: () -> Unit,
    placeholder: String,
    itemsCount: Int,
    initialScrollIndex: Int = 0,
    filterLogic: (Int, String) -> Boolean,
    onItemSelected: (Int) -> Unit,
    itemContent: @Composable (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredIndices = remember(searchQuery) {
        (0 until itemsCount).filter { index ->
            if (searchQuery.isBlank()) true else filterLogic(index, searchQuery)
        }
    }
    
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    LaunchedEffect(Unit) {
        if (initialScrollIndex in 0 until itemsCount) {
            val targetIndex = maxOf(0, initialScrollIndex - 2)
            listState.scrollToItem(targetIndex)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = GreenPrimaryLight,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = GreenPrimaryLight) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimaryLight,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        },
        text = {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                androidx.compose.foundation.lazy.LazyColumn(
                    state = listState,
                    modifier = Modifier.heightIn(max = 350.dp).fillMaxWidth()
                ) {
                    items(filteredIndices) { index ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onItemSelected(index) },
                            color = Color.Transparent
                        ) {
                            itemContent(index)
                        }
                        if (index != filteredIndices.last()) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                    if (filteredIndices.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("لا توجد نتائج", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}

// ─── Footer and Audio Overlay (kept for backward compat) ────

@Composable
fun QuranReadingFooter(
    surahNameToDisplay: String,
    sliderPosition: Float,
    onSliderValueChange: (Float) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun QuranAudioPlayerOverlay(
    surahNumber: Int,
    onClose: () -> Unit,
    viewModel: AudioPlayerViewModel = hiltViewModel()
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
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onClose() }
            )
            
            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ReciterCard(name: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.DarkGray,
        border = if (isSelected) BorderStroke(2.dp, GreenPrimaryLight) else null,
        modifier = modifier.aspectRatio(1f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
