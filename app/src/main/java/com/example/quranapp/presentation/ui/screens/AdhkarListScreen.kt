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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.theme.spacing
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdhkarListScreen(navController: NavController) {
    val spacing = MaterialTheme.spacing
    var showResetDialog by remember { mutableStateOf(false) }

    // Mock Data
    val categories = listOf(
        AdhkarCategoryData(1, "أذكار الصباح", "ابدأ يومك بذكر الله، طمأنينة وبركة حتى المساء", 1, 20),
        AdhkarCategoryData(2, "أذكار المساء", "اختم يومك بذكر الله، سَكينة وحفظ حتى الصباح", 8, 20),
        AdhkarCategoryData(3, "أذكار النوم", "اذكر الله قبل نومك، ونم على طمأنينة", 0, 10),
        AdhkarCategoryData(4, "أذكار الاستيقاظ", "ابدأ يومك بحمد الله مع أول استيقاظ", 2, 6),
        AdhkarCategoryData(5, "أذكار قبل الصلاة", "تهيأ للصلاة بذكر الله وحضور القلب", 0, 8)
    )

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
                // Refresh/Reset Button
                Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.5f)),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Progress",
                            tint = GreenPrimaryLight
                        )
                    }
                }

                Text(
                    text = "الأذكار",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Back Button
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

            // Subtitle
            Text(
                text = "اختر الذكر المناسب لوقتك، وداوم على ذكر الله في كل حين.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gridMargin, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // List of Categories
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gridMargin),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    AdhkarCategoryCard(category = category, navController = navController)
                }
            }
        }
    }

    if (showResetDialog) {
        AdhkarResetDialog(
            onConfirm = {
                // TODO: Reset logic
                showResetDialog = false
            },
            onDismiss = { showResetDialog = false }
        )
    }
}

@Composable
fun AdhkarCategoryCard(category: AdhkarCategoryData, navController: NavController) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = GreenPrimaryLight.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.AdhkarDetail.createRoute(category.id)) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Arrow
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary, // Gold color from design
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Pointing Left (forward in RTL conceptually, but design shows arrow pointing left)
                    contentDescription = "Enter",
                    tint = Color.White,
                    modifier = Modifier.padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Content
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Progress Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(60.dp)
            ) {
                CircularProgressIndicator(
                    progress = { category.progress.toFloat() / category.total.toFloat() },
                    modifier = Modifier.fillMaxSize(),
                    color = GreenPrimaryLight,
                    trackColor = GreenPrimaryLight.copy(alpha = 0.2f),
                    strokeWidth = 4.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${category.progress}/${category.total}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimaryLight
                    )
                    Text(
                        text = "قراءة اليوم",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun AdhkarResetDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Icon
                Surface(
                    shape = CircleShape,
                    color = GreenPrimaryLight.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.3f)),
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = GreenPrimaryLight,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "إعادة تعيين جميع مستوى التقدم",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "هل أنت متأكد من إعادة تعيين مستوى التقدم؟ سيتم مسح التقدم الحالي والبدء من الصفر.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cancel (Red outline)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onDismiss() }
                    ) {
                        Text(
                            text = "إلغاء",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    // Confirm (Green fill)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GreenPrimaryLight,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onConfirm() }
                    ) {
                        Text(
                            text = "إعادة التعيين",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

data class AdhkarCategoryData(
    val id: Int,
    val title: String,
    val description: String,
    val progress: Int,
    val total: Int
)
