package com.example.quranapp.presentation.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier,
    onMenuClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp), // Floating effect
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Navigation Bar Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp) // Sleeker height
                .shadow(
                    elevation = 20.dp, 
                    shape = RoundedCornerShape(32.dp),
                    spotColor = GreenPrimaryLight.copy(alpha = 0.15f) // Elegant tinted shadow
                ),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Right side items (in RTL, these appear on the visual right)
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    NavItem(
                        label = "المصحف",
                        icon = Icons.Default.MenuBook,
                        isSelected = currentRoute == Screen.QuranIndex.route || currentRoute?.startsWith("quran_reading") == true,
                        onClick = { navigateTo(navController, Screen.QuranIndex.route) }
                    )
                    NavItem(
                        label = "المهام",
                        icon = Icons.Default.CheckCircle,
                        isSelected = currentRoute == Screen.DailyTasks.route,
                        onClick = { navigateTo(navController, Screen.DailyTasks.route) }
                    )
                }

                // Space for middle FAB
                Spacer(modifier = Modifier.width(56.dp))

                // Left side items (in RTL, these appear on the visual left)
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    NavItem(
                        label = "الإحصائيات",
                        icon = Icons.Default.BarChart,
                        isSelected = currentRoute == Screen.Progress.route,
                        onClick = { navigateTo(navController, Screen.Progress.route) }
                    )
                    NavItem(
                        label = "الإعدادات",
                        icon = Icons.Default.Settings,
                        isSelected = currentRoute == Screen.Settings.route,
                        onClick = { navigateTo(navController, Screen.Settings.route) }
                    )
                }
            }
        }

        // Central Floating Action Button (Home)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-16).dp) // Pop out of the top slightly
                .size(56.dp) // Slightly smaller and more elegant
                .shadow(12.dp, CircleShape, spotColor = GreenPrimaryLight.copy(alpha = 0.3f))
                .border(2.dp, Color(0xFFC9A24D).copy(alpha = 0.5f), CircleShape) // Gold border
                .clip(CircleShape)
                .background(GreenPrimaryLight)
                .clickable { navigateTo(navController, Screen.Home.route) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
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
    
    // Animation states
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "scale"
    )
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(GreenPrimaryLight.copy(alpha = backgroundAlpha))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale)
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
            fontSize = 9.sp, // Slightly smaller to ensure fit
            maxLines = 1,
            softWrap = false
        )
        }
    }
}

private fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId)
        launchSingleTop = true
    }
}

