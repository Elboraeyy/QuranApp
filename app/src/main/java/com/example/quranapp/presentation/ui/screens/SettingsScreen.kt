package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quranapp.R
import androidx.compose.runtime.*
import com.example.quranapp.presentation.viewmodel.SettingsViewModel
import com.example.quranapp.domain.model.ThemeMode
import com.example.quranapp.domain.model.FontFamily
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.components.BottomNavigationBar
import com.example.quranapp.presentation.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val spacing = MaterialTheme.spacing
    val darkGreenBackground = MaterialTheme.colorScheme.primaryContainer
    
    val settings by viewModel.settings.collectAsState()
    
    var showThemeDialog by remember { mutableStateOf(false) }
    var showFontDialog by remember { mutableStateOf(false) }
    
    val menuItems = listOf(
        MenuItemData("المفضلة", Icons.Default.Bookmark) { 
            navController.navigate(Screen.Favorites.route)
        },
        MenuItemData("مظهر التطبيق", Icons.Default.Settings) { 
            showThemeDialog = true 
        },
        MenuItemData("نوع الخط", Icons.AutoMirrored.Filled.HelpOutline) { // Using as placeholder for font
            showFontDialog = true
        },
        MenuItemData("تواصل معنا", Icons.Default.Headset) { /* Handle support */ },
        MenuItemData("تقييم التطبيق", Icons.Default.ThumbUp) { /* Handle rate */ },
        MenuItemData("من نحن؟", Icons.Default.HelpOutline) { 
            navController.navigate(Screen.About.route) 
        },
        MenuItemData("شارك التطبيق", Icons.Default.Share) { /* Handle share */ }
    )

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = settings.theme,
            onThemeSelected = { 
                viewModel.updateTheme(it)
                showThemeDialog = false 
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showFontDialog) {
        FontSelectionDialog(
            currentFont = settings.fontFamily,
            onFontSelected = {
                viewModel.updateFontFamily(it)
                showFontDialog = false
            },
            onDismiss = { showFontDialog = false }
        )
    }

    Scaffold(
        containerColor = darkGreenBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.gridMargin),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Close Menu",
                                tint = Color(0xFFC9A24D)
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(menuItems) { item ->
                        MenuListItem(item)
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
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
            
            // Floating Bottom Navigation Bar
            BottomNavigationBar(
                navController = navController,
                currentRoute = Screen.Settings.route,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ThemeSelectionDialog(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "اختر مظهر التطبيق",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                ThemeOption("فاتح", ThemeMode.LIGHT, currentTheme == ThemeMode.LIGHT) { onThemeSelected(ThemeMode.LIGHT) }
                ThemeOption("داكن", ThemeMode.DARK, currentTheme == ThemeMode.DARK) { onThemeSelected(ThemeMode.DARK) }
                ThemeOption("تلقائي (حسب النظام)", ThemeMode.SYSTEM, currentTheme == ThemeMode.SYSTEM) { onThemeSelected(ThemeMode.SYSTEM) }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeOption(title: String, mode: ThemeMode, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSelectionDialog(
    currentFont: FontFamily,
    onFontSelected: (FontFamily) -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "اختر نوع الخط",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                FontOption("عثماني (Uthmani)", FontFamily.UTHMANI, currentFont == FontFamily.UTHMANI) { onFontSelected(FontFamily.UTHMANI) }
                FontOption("إندوباك (Indo-Pak)", FontFamily.INDO_PAK, currentFont == FontFamily.INDO_PAK) { onFontSelected(FontFamily.INDO_PAK) }
                FontOption("عادي (Standard)", FontFamily.NOOR_HAYAH, currentFont == FontFamily.NOOR_HAYAH) { onFontSelected(FontFamily.NOOR_HAYAH) }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun FontOption(title: String, font: FontFamily, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}

data class MenuItemData(
    val title: String, 
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

@Composable
fun MenuListItem(item: MenuItemData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { item.onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFC9A24D).copy(alpha = 0.15f),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color(0xFFC9A24D),
                modifier = Modifier.padding(12.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            tint = Color(0xFFC9A24D).copy(alpha = 0.6f)
        )
    }
}
