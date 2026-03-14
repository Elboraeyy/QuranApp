package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.quranapp.data.local.entity.UserStatsEntity
import com.example.quranapp.presentation.ui.theme.*
import com.example.quranapp.presentation.viewmodel.ReligiousTasksViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskHistoryScreen(
    navController: NavController,
    viewModel: ReligiousTasksViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("إرث الإنجازات", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = GreenPrimaryLight,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GreenPrimaryLight, BackgroundLight)
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Golden Ledger Summary
                AchievementSummaryCard(state.userStats)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    "إحصائيات الأسبوع",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimaryLight,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                WeeklyChart()
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    "أوسمة الفخر",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimaryLight,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Badges/Milestones
                BadgesGrid()
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    "سجل الخلود",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimaryLight,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Timeline of greatness
                LegacyTimeline()
            }
        }
    }
}

@Composable
fun AchievementSummaryCard(stats: UserStatsEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                null,
                tint = GoldSecondaryLight,
                modifier = Modifier.size(64.dp)
            )
            Text(
                "رصيد الحسنات المتوقع",
                color = TextPrimaryLight.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
            Text(
                "${stats.totalPoints} نقطة",
                style = MaterialTheme.typography.displaySmall,
                color = GreenPrimaryLight,
                fontWeight = FontWeight.Bold
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem(stats.currentStreak.toString(), "يوم متواصل")
                StatItem(stats.currentLevel.toString(), "المستوى")
                StatItem(stats.maxStreak.toString(), "أعلى سلسلة")
            }
        }
    }
}

@Composable
fun StatItem(valStr: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valStr, fontWeight = FontWeight.Bold, color = GreenPrimaryLight, fontSize = 20.sp)
        Text(label, fontSize = 12.sp, color = TextPrimaryLight.copy(alpha = 0.6f))
    }
}

@Composable
fun BadgesGrid() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Badge("فارس الفجر", true)
        Badge("صديق القرآن", true)
        Badge("المسبّح", false)
    }
}

@Composable
fun Badge(name: String, unLocked: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (unLocked) GoldSecondaryLight.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f))
                .border(2.dp, if (unLocked) GoldSecondaryLight else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Star,
                null,
                tint = if (unLocked) GoldSecondaryLight else Color.Gray.copy(alpha = 0.5f)
            )
        }
        Text(name, fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun LegacyTimeline() {
    Column {
        TimelineItem("اليوم", "أنجزت جميع صلوات الجماعة")
        TimelineItem("أمس", "ختمت جزء عمّ")
        TimelineItem("منذ ٣ أيام", "حققت سلسلة ٧ أيام فجر")
    }
}

@Composable
fun WeeklyChart() {
    val days = listOf("س", "ج", "خ", "أر", "ث", "إ", "ح")
    val progressData = listOf(0.4f, 0.8f, 0.6f, 1f, 0.7f, 0.5f, 0.9f) // Mock data for now
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEachIndexed { index, day ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(80.dp * progressData[index])
                            .clip(CircleShape)
                            .background(if (progressData[index] == 1f) GoldSecondaryLight else GreenPrimaryLight)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(day, fontSize = 10.sp, color = TextPrimaryLight.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
fun TimelineItem(time: String, title: String) {
    Row(modifier = Modifier.padding(vertical = 12.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(GoldSecondaryLight))
            Box(Modifier.width(2.dp).height(40.dp).background(GoldSecondaryLight.copy(alpha = 0.3f)))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(time, fontSize = 12.sp, color = TextPrimaryLight.copy(alpha = 0.5f))
            Text(title, fontWeight = FontWeight.SemiBold, color = TextPrimaryLight)
        }
    }
}
