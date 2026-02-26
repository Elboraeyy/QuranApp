package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quranapp.R
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.components.BottomNavigationBar
import com.example.quranapp.presentation.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) { // Keeping name SettingsScreen for NavGraph compatibility, but UI is Menu
    val spacing = MaterialTheme.spacing
    val darkGreenBackground = MaterialTheme.colorScheme.primaryContainer
    
    val menuItems = listOf(
        MenuItemData("المفضلة", Icons.Default.Bookmark),
        MenuItemData("الإعدادات", Icons.Default.Settings),
        MenuItemData("تواصل معنا", Icons.Default.Headset),
        MenuItemData("تقييم التطبيق", Icons.Default.ThumbUp),
        MenuItemData("من نحن؟", Icons.Default.HelpOutline),
        MenuItemData("شارك التطبيق", Icons.Default.Share)
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, Screen.Settings.route)
        },
        containerColor = darkGreenBackground
    ) { padding ->
        // Background Pattern Overlay Placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Actual pattern image would go here with low alpha
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.gridMargin),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header (Arrow Left in LTR, so Right in RTL to go back... wait, design has ArrowRight on top right. We use ArrowForward meaning 'back' or 'close menu')
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Close Menu",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // User Profile Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side (Arrow)
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward, // Should point left in RTL, so maybe ArrowBack theoretically, but let's use a standard chevron if possible.
                            contentDescription = "Edit Profile",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // User Info
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "مصطفى محمود",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "mostafaaaita@gmail.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    ) {
                        // Placeholder for User Avatar
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Menu Items
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(menuItems) { item ->
                        MenuListItem(item)
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        LogoutButton()
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Footer Logo
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "زاد مسلم",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "الدليل الإسلامي الشامل",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun MenuListItem(item: MenuItemData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click */ },
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(16.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun LogoutButton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle logout */ },
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "تسجيل الخروج",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error, // Red color
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(16.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

data class MenuItemData(val title: String, val icon: ImageVector)

