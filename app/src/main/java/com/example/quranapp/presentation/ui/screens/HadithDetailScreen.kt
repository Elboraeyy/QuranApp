package com.example.quranapp.presentation.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.quranapp.domain.model.Hadith
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import com.example.quranapp.presentation.viewmodel.HadithViewModel
import com.example.quranapp.util.ImageShareUtil
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithDetailScreen(
    navController: NavController,
    hadithId: Int,
    viewModel: HadithViewModel = hiltViewModel()
) {
    val hadiths by viewModel.allHadiths.collectAsState()
    val hadith = hadiths.find { it.id == hadithId }

    if (hadith == null) {
        // Fallback UI if not loaded yet or invalid ID
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GreenPrimaryLight)
        }
        return
    }

    val context = LocalContext.current
    val captureController = rememberCaptureController()
    val scope = rememberCoroutineScope()
    
    val isBookmarked by viewModel.isBookmarked(hadithId).collectAsState(initial = false)
    
    var isSourceExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("شرح الحديث", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleBookmark(hadith) }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (isBookmarked) GreenPrimaryLight else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = {
                        // Capture the wrapped composable as an image
                        captureController.capture()
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share as Image")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Main Hadith Card
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Capturable(
                        controller = captureController,
                        onCaptured = { bitmap, error ->
                            if (bitmap != null) {
                                ImageShareUtil.shareBitmap(context, bitmap.asAndroidBitmap())
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface) // Ensure solid background for image
                                .padding(24.dp)
                        ) {
                            Text(
                                text = hadith.text ?: "",
                                style = MaterialTheme.typography.headlineLarge,
                                fontFamily = com.example.quranapp.presentation.ui.theme.ScheherazadeNew,
                                lineHeight = 44.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isSourceExpanded = !isSourceExpanded }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "عن: ${hadith.narrator ?: "غير متوفر"}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = GreenPrimaryLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "المصادر",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = GreenPrimaryLight,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Icon(
                                            imageVector = if (isSourceExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Expand Source",
                                            tint = GreenPrimaryLight,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                AnimatedVisibility(visible = isSourceExpanded) {
                                    Text(
                                        text = hadith.source ?: "غير متوفر",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Explanation Section
            item {
                DetailSection(
                    title = "شرح وتفسير الحديث",
                    icon = Icons.Default.MenuBook,
                    content = {
                        Text(
                            text = hadith.explanation ?: "غير متوفر",
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 28.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                )
            }

            // Vocabulary Section (if any)
            if (hadith.vocabulary.isNotEmpty()) {
                item {
                    DetailSection(
                        title = "معاني الكلمات",
                        icon = Icons.Default.Info,
                        content = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                hadith.vocabulary.forEach { vocab ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Text(
                                            text = "${vocab.word}: ",
                                            fontWeight = FontWeight.Bold,
                                            color = GreenPrimaryLight,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = vocab.meaning,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // Life Applications Section (if any)
            if (hadith.lifeApplications.isNotEmpty()) {
                item {
                    DetailSection(
                        title = "الدروس والتطبيق الحياتي",
                        icon = Icons.Default.Lightbulb,
                        content = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                hadith.lifeApplications.forEachIndexed { index, app ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Surface(
                                            shape = CircleShape,
                                            color = GreenPrimaryLight.copy(alpha = 0.1f),
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${index + 1}",
                                                    color = GreenPrimaryLight,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = app,
                                            style = MaterialTheme.typography.bodyLarge,
                                            lineHeight = 24.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DetailSection(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = GreenPrimaryLight.copy(alpha = 0.1f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GreenPrimaryLight,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
