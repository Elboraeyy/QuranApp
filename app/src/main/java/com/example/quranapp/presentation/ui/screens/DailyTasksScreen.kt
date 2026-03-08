package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.components.BottomNavigationBar
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import com.example.quranapp.presentation.ui.theme.spacing
import java.text.SimpleDateFormat
import java.util.*

data class DailyTaskItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTasksScreen(navController: NavController) {
    val spacing = MaterialTheme.spacing
    
    // In a real app, this would come from a ViewModel and Room Database
    var tasks by remember {
        mutableStateOf(
            listOf(
                DailyTaskItem(1, "صلاة الفجر", "في وقتها جماعة"),
                DailyTaskItem(2, "أذكار الصباح", "التحصين اليومي"),
                DailyTaskItem(3, "ورد القرآن", "قراءة جزء على الأقل"),
                DailyTaskItem(4, "صلاة الضحى", "صلاة الأوابين"),
                DailyTaskItem(5, "صلاة الظهر", "في وقتها جماعة"),
                DailyTaskItem(6, "صلاة العصر", "في وقتها جماعة"),
                DailyTaskItem(7, "أذكار المساء", "التحصين اليومي"),
                DailyTaskItem(8, "صلاة المغرب", "في وقتها جماعة"),
                DailyTaskItem(9, "صلاة العشاء", "في وقتها جماعة"),
                DailyTaskItem(10, "صلاة الوتر", "ختام اليوم")
            )
        )
    }

    val completedCount = tasks.count { it.isCompleted }
    val progress = if (tasks.isEmpty()) 0f else completedCount.toFloat() / tasks.size

    val currentDate = SimpleDateFormat("EEEE، d MMMM", Locale("ar")).format(Date())

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Header Region
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.gridMargin, vertical = 24.dp)
                ) {
                    Text(
                        text = "المهام اليومية",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentDate,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        
                        // Mini Progress indicator
                        Surface(
                            shape = CircleShape,
                            color = GreenPrimaryLight.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$completedCount/${tasks.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = GreenPrimaryLight,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Main Progress Bar
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = GreenPrimaryLight,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )
                }

                // Tasks List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = spacing.gridMargin),
                    contentPadding = PaddingValues(bottom = 100.dp), // Space for bottom bar
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskItemCard(
                            task = task,
                            onToggle = { 
                                tasks = tasks.map { 
                                    if (it.id == task.id) it.copy(isCompleted = !it.isCompleted) else it 
                                }
                            }
                        )
                    }
                }
            }

            // Floating Bottom Navigation Bar
            BottomNavigationBar(
                navController = navController,
                currentRoute = Screen.DailyTasks.route,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun TaskItemCard(
    task: DailyTaskItem,
    onToggle: () -> Unit
) {
    val backgroundColor = if (task.isCompleted) {
        GreenPrimaryLight.copy(alpha = 0.05f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderColor = if (task.isCompleted) {
        GreenPrimaryLight.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (task.isCompleted) 0.dp else 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Checkbox/Status Indicator
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) GreenPrimaryLight else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (task.isCompleted) GreenPrimaryLight else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
