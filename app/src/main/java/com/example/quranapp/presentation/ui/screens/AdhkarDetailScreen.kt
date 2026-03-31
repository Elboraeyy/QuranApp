package com.example.quranapp.presentation.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.presentation.ui.theme.spacing
import com.example.quranapp.presentation.ui.theme.ScheherazadeNew
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quranapp.presentation.viewmodel.AdhkarViewModel
import com.example.quranapp.domain.model.AdhkarItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AdhkarDetailScreen(
    navController: NavController,
    categoryId: Int = 1,
    viewModel: AdhkarViewModel = hiltViewModel()
) {
    val category by viewModel.getCategoryById(categoryId).collectAsState(initial = null)
    val items = category?.items ?: emptyList()
    val pagerState = rememberPagerState(pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Text(
                    text = category?.title ?: "الأذكار",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Default.Refresh, "Reset", tint = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isSystemInDarkTheme()) MaterialTheme.colorScheme.background
                    else Color(0xFFFDF7E2)
                )
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (items.isNotEmpty()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    ) { page ->
                        val item = items[page]
                        DhikrCard(item = item, viewModel = viewModel)
                    }

                    // Tactile Counter Footer
                    CounterFooter(
                        currentItem = items.getOrNull(pagerState.currentPage),
                        totalItems = items.size,
                        currentPage = pagerState.currentPage,
                        onUpdateCount = { id, count -> viewModel.updateAdhkarCount(id, count) },
                        onNextPage = {
                            if (pagerState.currentPage < items.size - 1) {
                                coroutineScope.launch {
                                    delay(200)
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        onPrevPage = {
                            if (pagerState.currentPage > 0) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showResetDialog) {
        // Reset Dialog Implementation (OMITTED for brevity, but I will put a basic one)
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    items.forEach { viewModel.updateAdhkarCount(it.id, 0) }
                    showResetDialog = false
                }) { Text("تصفير الكل") }
            },
            title = { Text("تصفير العداد") },
            text = { Text("هل تريد تصفير عداد جميع الأذكار في هذا القسم؟") }
        )
    }
}

@Composable
fun DhikrCard(item: AdhkarItem, viewModel: AdhkarViewModel) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.TouchApp,
                null,
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = item.text,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = ScheherazadeNew,
                    lineHeight = 42.sp,
                    fontSize = 24.sp
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (item.reference.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = item.reference,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CounterFooter(
    currentItem: AdhkarItem?,
    totalItems: Int,
    currentPage: Int,
    onUpdateCount: (Int, Int) -> Unit,
    onNextPage: () -> Unit,
    onPrevPage: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Prev
            IconButton(onClick = onPrevPage, enabled = currentPage > 0) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = if(currentPage > 0) MaterialTheme.colorScheme.primary else Color.Transparent)
            }

            // Main Counter
            currentItem?.let { item ->
                val progress by animateFloatAsState(
        targetValue = if (item.targetCount > 0) item.currentCount.toFloat() / item.targetCount.toFloat() else 0f
    )
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = if (item.currentCount >= item.targetCount) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                    shadowElevation = 8.dp,
                    onClick = {
                        if (item.currentCount < item.targetCount) {
                            onUpdateCount(item.id, item.currentCount + 1)
                            if (item.currentCount + 1 >= item.targetCount) onNextPage()
                        }
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            color = Color.White.copy(alpha = 0.1f),
                            strokeWidth = 4.dp
                        )
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            color = if (item.currentCount >= item.targetCount) MaterialTheme.colorScheme.primary else Color.White,
                            strokeWidth = 4.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${item.currentCount}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = if (item.currentCount >= item.targetCount) MaterialTheme.colorScheme.primary else Color.White)
                            Text("من ${item.targetCount}", style = MaterialTheme.typography.labelSmall, color = if (item.currentCount >= item.targetCount) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            // Next
            IconButton(onClick = onNextPage, enabled = currentPage < totalItems - 1) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = if(currentPage < totalItems - 1) MaterialTheme.colorScheme.primary else Color.Transparent)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("${currentPage + 1} / $totalItems", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    }
}
