package com.example.quranapp.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.quranapp.presentation.ui.theme.quranic
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

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
    startPage: Int = 1,
    viewModel: SurahDetailViewModel = hiltViewModel()
) {
    val surah by viewModel.surah.collectAsState()
    val qcfPage by viewModel.qcfPage.collectAsState()
    val isBookmarked by viewModel.isBookmarked.collectAsState()
    val spacing = MaterialTheme.spacing
    var isAudioPlayerOpen by remember { mutableStateOf(false) }
    var showOverlay by remember { mutableStateOf(false) }

    val errorMessage by viewModel.errorMessage.collectAsState()

    val pagerState = rememberPagerState(initialPage = startPage - 1, pageCount = { 604 })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(pagerState.currentPage) {
        viewModel.loadPage(pagerState.currentPage + 1)
    }

    val surahNameToDisplay = surah?.nameArabic ?: ""

    // Juz start pages (standard Mushaf)
    val juzStartPages = remember {
        listOf(1,22,42,62,82,102,122,142,162,182,202,222,242,262,282,302,322,332,342,362,382,402,422,442,462,482,502,522,542,562,582,604)
    }

    // Calculate current juz based on page number
    val currentPage = pagerState.currentPage + 1
    val currentJuz = remember(currentPage) {
        juzStartPages.indexOfLast { currentPage >= it } + 1
    }

    // Dialog states
    var showSurahPicker by remember { mutableStateOf(false) }
    var showJuzPicker by remember { mutableStateOf(false) }
    var showPagePicker by remember { mutableStateOf(false) }

    // Full-screen layout with overlays
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) { showOverlay = !showOverlay }
    ) {
        // Main content: Header + Pager
        Column(modifier = Modifier.fillMaxSize()) {
            // SURAH / JUZ HEADER — always visible
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Surah Name (Right side in RTL)
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
                    
                    // Juz Number (Left side in RTL)
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

            // QURAN PAGER — fills remaining space
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                ) { pageIndex ->
                    val actualPage = pageIndex + 1

                    // Load page-specific QCF font
                    val fontPath = String.format("fonts/QCF_P%03d.TTF", actualPage)
                    val typeface = remember(actualPage) {
                        try {
                            Typeface.createFromAsset(context.assets, fontPath)
                        } catch (e: Exception) {
                            Typeface.DEFAULT
                        }
                    }
                    val qcfFontFamily = remember(typeface) { FontFamily(ComposeTypeface(typeface)) }
                    
                    val bismillahTypeface = remember {
                        try {
                            Typeface.createFromAsset(context.assets, "fonts/me_quran.ttf")
                        } catch (e: Exception) {
                            Typeface.DEFAULT
                        }
                    }
                    val bismillahFontFamily = remember(bismillahTypeface) { FontFamily(ComposeTypeface(bismillahTypeface)) }

                    val surahsList = remember {
                        try {
                            val jsonString = context.assets.open("surahs.json").bufferedReader().use { it.readText() }
                            val jsonArray = org.json.JSONArray(jsonString)
                            val map = mutableMapOf<Int, String>()
                            for (i in 0 until jsonArray.length()) {
                                val obj = jsonArray.getJSONObject(i)
                                map[obj.getInt("number")] = obj.getString("name")
                            }
                            map
                        } catch (e: Exception) {
                            emptyMap<Int, String>()
                        }
                    }

                    // Render page lines
                    if (qcfPage?.pageNumber == actualPage && qcfPage?.lines?.isNotEmpty() == true) {
                        BoxWithConstraints(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val availableWidth = maxWidth
                            val fontSize = (availableWidth.value / 16f).sp
                            val lineHeight = fontSize * 1.8f

                            Column(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                qcfPage?.lines?.forEach { line ->
                                    if (line.isBismillah) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data("file:///android_asset/surah_names/0.svg")
                                                    .decoderFactory(SvgDecoder.Factory())
                                                    .build(),
                                                contentDescription = "Bismillah",
                                                modifier = Modifier.fillMaxWidth(0.9f).heightIn(max = 40.dp),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                            )
                                        }
                                    } else if (line.surahNumber != null && line.surahNumber!! > 0) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data("file:///android_asset/surah_names/${line.surahNumber}.svg")
                                                    .decoderFactory(SvgDecoder.Factory())
                                                    .build(),
                                                contentDescription = "Surah Name",
                                                modifier = Modifier.fillMaxWidth().heightIn(max = 60.dp),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                            )
                                        }

                                    } else {
                                        Text(
                                            text = line.text,
                                            fontFamily = qcfFontFamily,
                                            fontSize = fontSize,
                                            lineHeight = lineHeight,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            maxLines = 1,
                                            modifier = Modifier.fillMaxWidth()
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

        // 2. PAGE NUMBER — always visible at the bottom
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

        // 3. TOP BAR OVERLAY — appears/disappears on tap
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
                        .padding(horizontal = spacing.gridMargin, vertical = 12.dp)
                        .padding(top = 32.dp), // status bar padding
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
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

                    // Surah Name
                    Text(
                        text = surahNameToDisplay,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    // Action Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Share
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFC9A24D).copy(alpha = 0.1f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = {
                                val shareText = "استمع واقرأ سورة ${surah?.nameArabic ?: ""} عبر تطبيق زاد مسلم"
                                val sendIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(android.content.Intent.createChooser(sendIntent, "مشاركة"))
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
                        // Audio
                        Surface(
                            shape = CircleShape,
                            color = if (isAudioPlayerOpen) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else Color(0xFFC9A24D).copy(alpha = 0.1f),
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
                    }
                }
            }
        }

        // 4. BOTTOM BAR OVERLAY — appears/disappears on tap
        AnimatedVisibility(
            visible = showOverlay,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column {
                QuranReadingFooter(
                    surahNameToDisplay = surahNameToDisplay,
                    sliderPosition = (pagerState.currentPage.toFloat() / 603f),
                    onSliderValueChange = { /* Could navigate to specific page */ }
                )
            }
        }

        // 5. AUDIO PLAYER OVERLAY
        AnimatedVisibility(
            visible = isAudioPlayerOpen,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            QuranAudioPlayerOverlay(
                surahNumber = surah?.number ?: 1,
                onClose = { isAudioPlayerOpen = false }
            )
        }
    }

    // Surah names for the picker
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

    // Surah start pages mapping
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

    // EXTRACTED PREMIUM PICKERS
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

// Reusable Premium Dialog Component with Search
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
            // Subtracting 2 to roughly center the selected item, max out at 0 so it doesn't crash on negative indices
            val targetIndex = maxOf(0, initialScrollIndex - 2)
            listState.scrollToItem(targetIndex)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp), // Extremely rounded for premium feel
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
                // Search Field
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
