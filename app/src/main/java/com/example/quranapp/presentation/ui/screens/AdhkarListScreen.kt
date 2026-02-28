package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quranapp.presentation.viewmodel.AdhkarViewModel
import com.example.quranapp.domain.model.AdhkarCategory

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AdhkarListScreen(
    navController: NavController,
    viewModel: AdhkarViewModel = hiltViewModel()
) {
    val spacing = MaterialTheme.spacing
    var showResetDialog by remember { mutableStateOf(false) }
    
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
                    text = "الأذكار",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Refresh/Reset Button (Left in Arabic)
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

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimaryLight)
                }
            } else {
                // Group Categories
                val groupedCategories = remember(categories) {
                    categories.groupBy { getMetaCategoryForId(it.id) }
                }

                // Define preferred order for meta categories
                val preferredOrder = listOf(
                    "أذكار اليوم والليلة",
                    "أذكار الصلاة",
                    "أذكار الوضوء والمسجد",
                    "أذكار المسكن",
                    "أذكار اللباس",
                    "أذكار الخلاء",
                    "الطعام والشراب والصيام",
                    "السفر والتنقل",
                    "العواطف والمشاعر",
                    "الحج والعمرة",
                    "المرض والموت",
                    "الطبيعة والظواهر",
                    "الأسرة والعلاقات",
                    "متفرقات وتسابيح"
                )

                // Sort the groups based on the preferred order
                val sortedGroups = groupedCategories.entries.sortedBy { entry ->
                    val index = preferredOrder.indexOf(entry.key)
                    if (index != -1) index else preferredOrder.size
                }

                // List of Categories with Sticky Headers
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.gridMargin),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    sortedGroups.forEach { (metaCategory, sectionCategories) ->
                        stickyHeader {
                            Surface(
                                color = MaterialTheme.colorScheme.background,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = metaCategory,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }

                        items(sectionCategories) { category ->
                            AdhkarCategoryCard(category = category, navController = navController)
                        }
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AdhkarResetDialog(
            onConfirm = {
                viewModel.resetAllProgress()
                showResetDialog = false
            },
            onDismiss = { showResetDialog = false }
        )
    }
}

@Composable
fun AdhkarCategoryCard(category: AdhkarCategory, navController: NavController) {
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
            // Right Side: Progress Circle (Starts on Right in RTL)
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

            Spacer(modifier = Modifier.width(16.dp))

            // Middle: Text Content
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start // Right-aligned in RTL
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
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Left Side: Navigation Arrow (Ends on Left in RTL)
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, // Points Left in design
                    contentDescription = "Enter",
                    tint = Color.White,
                    modifier = Modifier.padding(6.dp)
                )
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

fun getMetaCategoryForId(id: Int): String {
    return when (id) {
        1, 2, 3, 28, 29, 30, 31, 134 -> "أذكار اليوم والليلة"
        16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 32, 33, 41 -> "أذكار الصلاة"
        9, 10, 13, 14, 15 -> "أذكار الوضوء والمسجد"
        11, 12 -> "أذكار المسكن"
        4, 5, 6 -> "أذكار اللباس"
        7, 8 -> "أذكار الخلاء"
        67, 68, 69, 70, 71, 72, 73, 74, 75 -> "الطعام والشراب والصيام"
        94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104 -> "السفر والتنقل"
        34, 35, 36, 37, 38, 39, 40, 42, 45, 81, 121, 123, 124, 125, 105, 122 -> "العواطف والمشاعر"
        114, 115, 116, 117, 118, 119, 120 -> "الحج والعمرة"
        48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59 -> "المرض والموت"
        60, 61, 62, 63, 64, 65, 66 -> "الطبيعة والظواهر"
        46, 47, 78, 79, 80 -> "الأسرة والعلاقات"
        43, 44, 76, 77, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 106, 107, 108, 109, 110, 111, 112, 113, 126, 127, 128, 129, 130, 131, 132, 133, 135 -> "متفرقات وتسابيح"
        else -> "متفرقات وتسابيح"
    }
}
