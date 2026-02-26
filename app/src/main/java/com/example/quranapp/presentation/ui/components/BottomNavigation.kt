package com.example.quranapp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String?, onMenuClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp), // Total height includes space for FAB overlap
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Navigation Bar Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 16.dp, 
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side items
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    NavItem(
                        label = "الرئيسية",
                        icon = Icons.Default.Home,
                        isSelected = currentRoute == Screen.Home.route,
                        onClick = { navigateTo(navController, Screen.Home.route) }
                    )
                    NavItem(
                        label = "الصلاة",
                        icon = Icons.Default.Mosque, // Placeholder for specific prayer icon
                        isSelected = currentRoute == Screen.PrayerTimes.route,
                        onClick = { navigateTo(navController, Screen.PrayerTimes.route) }
                    )
                }

                // Space for middle FAB
                Spacer(modifier = Modifier.width(72.dp))

                // Right side items
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    NavItem(
                        label = "الإشعارات",
                        icon = Icons.Default.Notifications,
                        isSelected = currentRoute == Screen.Notifications.route,
                        hasBadge = true,
                        badgeCount = 9,
                        onClick = { navigateTo(navController, Screen.Notifications.route) }
                    )
                    NavItem(
                        label = "القائمة",
                        icon = Icons.Default.Menu,
                        isSelected = currentRoute == Screen.Settings.route, // Mapping menu to settings
                        onClick = { 
                            if (onMenuClick != null) {
                                onMenuClick()
                            } else {
                                navigateTo(navController, Screen.Settings.route) 
                            }
                        }
                    )
                }
            }
        }

        // Central Floating Action Button (Qibla)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 8.dp) // Move slightly down from top to overlap nicely
                .size(64.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(GreenPrimaryLight)
                .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape)
                .clickable { navigateTo(navController, Screen.Qibla.route) },
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for Kaaba/Compass icon
            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = "Qibla",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    hasBadge: Boolean = false,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val color = if (isSelected) GreenPrimaryLight else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BadgedBox(
            badge = {
                if (hasBadge) {
                    Badge(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ) {
                        Text(badgeCount.toString())
                    }
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 10.sp
        )
    }
}

private fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId)
        launchSingleTop = true
    }
}

