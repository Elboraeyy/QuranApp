package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SignLanguage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.components.BottomNavigationBar
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import com.example.quranapp.presentation.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val spacing = MaterialTheme.spacing
    
    // Categories with associated icons
    val categories = listOf(
        FilterCategory("الجميع", Icons.Default.GridView),
        FilterCategory("إشعارات الصلاة", Icons.Default.Person), 
        FilterCategory("إشعارات الأذكار", Icons.Default.SignLanguage),
        FilterCategory("إشعارات الأحاديث", Icons.Default.Book)
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, Screen.Notifications.route)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            NotificationsHeader(onBack = { navController.popBackStack() })
            
            // Filters
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gridMargin, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(categories) { category ->
                    FilterChipCustom(
                        category = category,
                        isSelected = category == selectedCategory,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            // Notification List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.gridMargin),
                contentPadding = PaddingValues(bottom = 80.dp), // Padding for bottom nav
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Today Group
                item {
                    DateDivider("اليوم")
                }
                
                item {
                    NotificationCard(
                        title = "حان الآن موعد صلاة الفجر لا تنس ركعتي السُّنة",
                        subtitle = "«أَقِمِ الصَّلَاةَ لِذِكْرِي»",
                        timeStr = "منذ ساعة و 5 دقائق",
                        isUnread = true,
                        icon = Icons.Default.Person
                    )
                }
                
                item {
                    NotificationCard(
                        title = "تبقَّى 10 دقائق على صلاة الظهر",
                        subtitle = "الصلاة نور لقلبك وراحة ليومك",
                        timeStr = "منذ 3 ساعات",
                        isUnread = false,
                        icon = Icons.Default.Person
                    )
                }
                
                item {
                    NotificationCard(
                        title = "أذكار الصباح",
                        subtitle = "ابدأ يومك بحفظ من الله وطمأنينة",
                        timeStr = "منذ ساعتين",
                        isUnread = true,
                        icon = Icons.Default.SignLanguage
                    )
                }

                // Previous Date Group
                item {
                    DateDivider("3 يناير 2026")
                }
                
                item {
                    NotificationCard(
                        title = "إشعارات الأحاديث",
                        subtitle = "«أَحَبُّ الْكَلَامِ إِلَى اللَّهِ: سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ...»",
                        timeStr = "منذ 3 أيام",
                        isUnread = false,
                        icon = Icons.Default.Book
                    )
                }
                
                item {
                    NotificationCard(
                        title = "أذكار الصباح",
                        subtitle = "ابدأ يومك بحفظ من الله وطمأنينة",
                        timeStr = "منذ يومين",
                        isUnread = true,
                        icon = Icons.Default.SignLanguage
                    )
                }
            }
        }
    }
}

data class FilterCategory(val name: String, val icon: ImageVector)

@Composable
fun NotificationsHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left spacer for balance
        Spacer(modifier = Modifier.size(48.dp))

        Text(
            text = "الإشعارات",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Back Button
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.size(48.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun FilterChipCustom(category: FilterCategory, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) GreenPrimaryLight else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val borderThickness = if (isSelected) 0.dp else 1.dp
    val borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)

    Surface(
        shape = RoundedCornerShape(12.dp), // Slightly square-ish pills based on design
        color = backgroundColor,
        border = BorderStroke(borderThickness, borderColor),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon is shown on all tabs in the new design
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun DateDivider(dateStr: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Text(
                text = dateStr,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NotificationCard(
    title: String,
    subtitle: String,
    timeStr: String,
    isUnread: Boolean,
    icon: ImageVector
) {
    val backgroundColor = if (isUnread) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val borderColor = if (isUnread) GreenPrimaryLight.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth() // Subtle elevation for unread could be added here
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Title & Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f).padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Icon directly without the background box, based on image
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isUnread) GreenPrimaryLight else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth().padding(end = 48.dp) // Align under text
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bottom Row: Time and Unread Dot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Unread Dot + Time on Left side (due to RTL layout matching design visual)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isUnread) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(GreenPrimaryLight, CircleShape)
                        )
                    } else {
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
