package com.example.quranapp.presentation.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.quranapp.data.local.entity.TaskPeriod
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.components.BottomNavigationBar
import com.example.quranapp.presentation.ui.theme.*
import com.example.quranapp.presentation.viewmodel.ReligiousTasksViewModel
import com.example.quranapp.presentation.viewmodel.TaskWithCompletion
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReligiousTasksScreen(
    navController: NavController,
    viewModel: ReligiousTasksViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val spacing = MaterialTheme.spacing
    val currentDate = SimpleDateFormat("EEEE، d MMMM", Locale("ar")).format(Date())

    Scaffold(
        containerColor = Color.Transparent, // We'll use a custom gradient background
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = Screen.DailyTasks.route
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GreenPrimaryLight,
                            BackgroundLight
                        )
                    )
                )
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Region
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "المهام الإيمانية",
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        IconButton(
                            onClick = { navController.navigate(Screen.TaskHistory.route) },
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .clip(CircleShape)
                        ) {
                            Icon(Icons.Default.History, "History", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Level and Streak Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Level Info
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "مستوى ${state.userStats.currentLevel}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "${state.userStats.currentXP} / ${state.userStats.nextLevelXP} XP",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = (state.userStats.currentXP.toFloat() / state.userStats.nextLevelXP),
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = GoldSecondaryLight,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                        }

                        // Streak Info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.Whatshot,
                                null,
                                tint = Color(0xFFFF5722),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "${state.userStats.currentStreak}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Progress Glass Card
                    PremiumProgressCard(state.completionPercentage)
                }

                // Tabs for Periods
                ScrollableTabRow(
                    selectedTabIndex = state.selectedPeriod.ordinal,
                    containerColor = Color.Transparent,
                    contentColor = GreenPrimaryLight,
                    edgePadding = 24.dp,
                    divider = {},
                    indicator = { tabPositions ->
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[state.selectedPeriod.ordinal])
                                .height(3.dp)
                                .clip(CircleShape)
                                .background(GreenPrimaryLight)
                        )
                    }
                ) {
                    TaskPeriod.entries.forEach { period ->
                        Tab(
                            selected = state.selectedPeriod == period,
                            onClick = { viewModel.selectPeriod(period) },
                            text = {
                                Text(
                                    text = when(period) {
                                        TaskPeriod.DAILY -> "يومية"
                                        TaskPeriod.WEEKLY -> "أسبوعية"
                                        TaskPeriod.MONTHLY -> "شهرية"
                                        TaskPeriod.YEARLY -> "سنوية"
                                    },
                                    fontWeight = if (state.selectedPeriod == period) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                // Tasks List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.tasks, key = { it.task.id }) { taskWithCompletion ->
                        TaskGlassCard(
                            taskWithCompletion = taskWithCompletion,
                            onToggle = { viewModel.toggleTask(it) }
                        )
                    }
                    
                    item {
                        Button(
                            onClick = { navController.navigate(Screen.TaskCustomization.route) },
                            modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldSecondaryLight),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Add, null)
                            Spacer(Modifier.width(8.dp))
                            Text("تخصيص المهام", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumProgressCard(progress: Float) {
    val animatedProgress by animateFloatAsState(targetValue = progress)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.size(70.dp),
                    color = GoldSecondaryLight,
                    strokeWidth = 6.dp,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column {
                Text(
                    text = "إنجازك اليوم",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = if (progress == 1f) "ما شاء الله! يومك مبارك" else "استمر، بقليل من الجهد تصل للكمال",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun TaskGlassCard(
    taskWithCompletion: TaskWithCompletion,
    onToggle: (Long) -> Unit
) {
    val isCompleted = taskWithCompletion.completion?.isCompleted == true
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(taskWithCompletion.task.id) },
        shape = RoundedCornerShape(20.dp),
        color = if (isCompleted) GreenPrimaryLight.copy(alpha = 0.1f) else Color.White,
        shadowElevation = 2.dp,
        border = if (isCompleted) BorderStroke(1.dp, GoldSecondaryLight.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = taskWithCompletion.task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (isCompleted) TextPrimaryLight.copy(alpha = 0.5f) else TextPrimaryLight,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = taskWithCompletion.task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryLight.copy(alpha = 0.6f)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) GoldSecondaryLight else Color.Transparent)
                    .border(2.dp, if (isCompleted) GoldSecondaryLight else GreenPrimaryLight.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
