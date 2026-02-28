package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.draw.drawWithContent
import kotlinx.coroutines.delay
import com.example.quranapp.utils.ShareUtils
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.presentation.ui.theme.spacing
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import com.example.quranapp.presentation.ui.theme.ScheherazadeNew

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quranapp.presentation.viewmodel.AdhkarViewModel
import com.example.quranapp.domain.model.AdhkarItem
import com.example.quranapp.domain.model.AdhkarCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdhkarDetailScreen(
    navController: NavController,
    categoryId: Int = 1,
    viewModel: AdhkarViewModel = hiltViewModel()
) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val category by viewModel.getCategoryById(categoryId).collectAsState(initial = null)
    var currentItemIndex by remember { mutableStateOf(0) }
    var showResetDialog by remember { mutableStateOf(false) }

    val items = category?.items ?: emptyList()
    val pagerState = rememberPagerState(pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()

    val graphicsLayer = rememberGraphicsLayer()
    var dhikrToCapture by remember { mutableStateOf<AdhkarItem?>(null) }

    // Sync pager state changes back to currentItemIndex for footer UI
    LaunchedEffect(pagerState.currentPage) {
        currentItemIndex = pagerState.currentPage
    }

    val currentItem = items.getOrNull(currentItemIndex)

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
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
                    text = category?.title ?: "الأذكار",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Refresh Button (Left in Arabic)
                Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.5f)),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Count",
                            tint = GreenPrimaryLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showResetDialog) {
                AlertDialog(
                    onDismissRequest = { showResetDialog = false },
                    title = {
                        Text(
                            text = "تصفير العداد",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        val categoryName = category?.title ?: "هذا القسم"
                        Text(
                            text = "هل تريد تصفير عداد هذا الذكر فقط أم تصفير جميع أذكار $categoryName؟",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                currentItem?.let { viewModel.updateAdhkarCount(it.id, 0) }
                                showResetDialog = false
                            }
                        ) {
                            Text("هذا الذكر", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                category?.items?.forEach {
                                    viewModel.updateAdhkarCount(it.id, 0)
                                }
                                showResetDialog = false
                            }
                        ) {
                            Text("جميع الأذكار", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            if (category == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimaryLight)
                }
            } else if (items.isNotEmpty()) {
                // Main Content Area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) { page ->
                            val pageItem = items[page]
                            // Dhikr Card
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 700.dp)
                                    .padding(horizontal = spacing.gridMargin)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Decorative Icon (Rosary/Misbaha)
                                    Surface(
                                        shape = CircleShape,
                                        color = GreenPrimaryLight.copy(alpha = 0.1f),
                                        border = BorderStroke(
                                            1.dp,
                                            GreenPrimaryLight.copy(alpha = 0.3f)
                                        ),
                                        modifier = Modifier.size(56.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TouchApp,
                                            contentDescription = null,
                                            tint = GreenPrimaryLight,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    val isVerse =
                                        pageItem.text.contains("﴿") || pageItem.reference.contains("سورة")

                                    if (isVerse) {
                                        // Verse specific styling
                                        // Ornate brackets: ﴿ (start) and ﴾ (end)
                                        val verseText = if (!pageItem.text.trim().startsWith("﴿")) {
                                            "﴿${pageItem.text.trim()}﴾"
                                        } else {
                                            pageItem.text
                                        }

                                        val dynamicFontSize =
                                            if (verseText.length > 300) 20.sp else 24.sp
                                        val dynamicLineHeight =
                                            if (verseText.length > 300) 36.sp else 42.sp

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = verseText,
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    fontFamily = ScheherazadeNew,
                                                    lineHeight = dynamicLineHeight,
                                                    fontSize = dynamicFontSize,
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            if (pageItem.reference.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text(
                                                    text = pageItem.reference,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.primary.copy(
                                                        alpha = 0.7f
                                                    ),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            horizontal = 12.dp,
                                                            vertical = 4.dp
                                                        ),
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    } else {
                                        // Normal Dhikr Text
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            val dynamicFontSize =
                                                if (pageItem.text.length > 300) 18.sp else 22.sp
                                            val dynamicLineHeight =
                                                if (pageItem.text.length > 300) 30.sp else 36.sp

                                            Text(
                                                text = pageItem.text,
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    lineHeight = dynamicLineHeight,
                                                    fontSize = dynamicFontSize
                                                ),
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            if (pageItem.reference.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Text(
                                                    text = pageItem.reference,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.primary.copy(
                                                        alpha = 0.7f
                                                    ),
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }

                                Spacer(modifier = Modifier.height(32.dp))

                                // Bottom Actions inside Card
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    // Actions (Share, Bookmark)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        val isFavorite by viewModel.observeIsFavorite(pageItem.id).collectAsState(initial = false)

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.Transparent,
                                            border = BorderStroke(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            ),
                                            modifier = Modifier.size(36.dp),
                                            onClick = { viewModel.toggleFavorite(pageItem.id) }
                                        ) {
                                            Icon(
                                                imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                contentDescription = "Bookmark",
                                                tint = if (isFavorite) GreenPrimaryLight else MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.padding(6.dp)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.Transparent,
                                            border = BorderStroke(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            ),
                                            modifier = Modifier.size(36.dp),
                                            onClick = {
                                                dhikrToCapture = pageItem
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share",
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.padding(6.dp)
                                            )
                                        }
                                    }

                                    // Times Count
                                    Text(
                                        text = "${pageItem.targetCount} مرات",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Counter Selection (Footer)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.gridMargin)
                ) {
                    // Previous Arrow (Back to previous dhikr)
                    Box(modifier = Modifier.align(Alignment.CenterStart)) {
                        Surface(
                            shape = CircleShape,
                            color = if (pagerState.currentPage > 0) MaterialTheme.colorScheme.secondary else Color.LightGray.copy(
                                alpha = 0.3f
                            ),
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    if (pagerState.currentPage > 0) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                    }
                                },
                                enabled = pagerState.currentPage > 0
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Main Counter Button & Info
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        currentItem?.let { item ->
                            Surface(
                                shape = CircleShape,
                                color = if (item.currentCount >= item.targetCount) Color.Gray else GreenPrimaryLight,
                                shadowElevation = 8.dp,
                                modifier = Modifier.size(100.dp),
                                onClick = {
                                    if (item.currentCount < item.targetCount) {
                                        val nextCount = item.currentCount + 1
                                        viewModel.updateAdhkarCount(item.id, nextCount)

                                        // Auto-next logic: move to next item immediately if current one finished
                                        if (nextCount >= item.targetCount) {
                                            if (pagerState.currentPage < items.size - 1) {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                                }
                                            }
                                        }
                                    }
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        progress = { 1f },
                                        modifier = Modifier.fillMaxSize(),
                                        color = Color.White.copy(alpha = 0.2f),
                                        strokeWidth = 6.dp
                                    )
                                    CircularProgressIndicator(
                                        progress = { item.currentCount.toFloat() / item.targetCount.toFloat() },
                                        modifier = Modifier.fillMaxSize(),
                                        color = Color.White,
                                        strokeWidth = 6.dp
                                    )

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.TouchApp,
                                            contentDescription = "Tap",
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp).padding(bottom = 4.dp)
                                        )
                                        Text(
                                            text = "${item.currentCount}/${item.targetCount}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dhikr Index
                        Text(
                            text = "${pagerState.currentPage + 1} / ${items.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Next Arrow (Forward to next dhikr)
                    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                            Surface(
                                shape = CircleShape,
                                color = if (pagerState.currentPage < items.size - 1) MaterialTheme.colorScheme.secondary else Color.LightGray.copy(
                                    alpha = 0.3f
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        if (pagerState.currentPage < items.size - 1) {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                            }
                                        }
                                    },
                                    enabled = pagerState.currentPage < items.size - 1
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Next",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
        // Capture UI
        if (dhikrToCapture != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GreenPrimaryLight.copy(alpha = 0.1f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = GreenPrimaryLight,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        val shareItem = dhikrToCapture!!
                        val isVerse = shareItem.text.contains("﴿") || shareItem.reference.contains("سورة")
                        if (isVerse) {
                            val verseText = if (!shareItem.text.trim().startsWith("﴿")) "﴿${shareItem.text.trim()}﴾" else shareItem.text
                            Text(
                                text = verseText,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = ScheherazadeNew,
                                    lineHeight = 36.sp,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = shareItem.text,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    lineHeight = 30.sp,
                                    fontSize = 18.sp
                                ),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (shareItem.reference.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = shareItem.reference,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = GreenPrimaryLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تطبيق القرآن الكريم",
                                style = MaterialTheme.typography.labelMedium,
                                color = GreenPrimaryLight
                            )
                        }
                    }
                }
            }

            LaunchedEffect(dhikrToCapture) {
                delay(300)
                try {
                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                    ShareUtils.shareBitmap(context, bitmap, "مشاركة الذكر")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                dhikrToCapture = null
            }
        }
    }
