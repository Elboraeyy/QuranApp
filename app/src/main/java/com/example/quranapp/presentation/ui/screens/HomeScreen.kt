package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.components.BottomNavigationBar
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import com.example.quranapp.presentation.ui.theme.spacing
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.quranapp.presentation.viewmodel.PrayerTimesViewModel
import com.example.quranapp.presentation.viewmodel.SettingsViewModel
import com.example.quranapp.domain.model.PrayerTime
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import com.example.quranapp.domain.model.ThemeMode

@Composable
fun HomeScreen(
    navController: NavController,
    prayerViewModel: PrayerTimesViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val currentRoute = Screen.Home.route
    val spacing = MaterialTheme.spacing
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val todayPrayerTime by prayerViewModel.today.collectAsState()
    val locationName by prayerViewModel.locationName.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
                prayerViewModel.fetchPrayerTimes()
            }
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.Transparent,
                drawerShape = RoundedCornerShape(0.dp)
            ) {
                DrawerContent(navController = navController) {
                    scope.launch { drawerState.close() }
                }
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
        ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(spacing.gridMargin),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                TopSection(
                    locationName = locationName,
                    currentTheme = settings.theme,
                    onThemeChange = { settingsViewModel.updateTheme(it) }
                )
            }

            item {
                PrayerTimesCard(prayerTime = todayPrayerTime)
            }

            item {
                ToolsSection(navController = navController)
            }

            item {
                HadithSection()
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Extra space for FAB overlap
            }
        }
    }
}
}

@Composable
fun TopSection(
    locationName: String = "القاهرة، مصر",
    currentTheme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Location Pill (Start/Right in RTL)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color.Red,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = locationName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Change Location",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Theme Toggle Pill (End/Left in RTL)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            val isDarkTheme = when (currentTheme) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isDarkTheme) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .clickable { onThemeChange(ThemeMode.DARK) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NightlightRound,
                        contentDescription = "Dark Theme",
                        tint = if (isDarkTheme) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (!isDarkTheme) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .clickable { onThemeChange(ThemeMode.LIGHT) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Light Theme",
                        tint = if (!isDarkTheme) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PrayerTimesCard(prayerTime: PrayerTime?) {
    // Replicating the sunset/dark gradient from the design
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2A2118), // Dark brown/black at top
            Color(0xFF5D3A1A), // Warm brown in middle
            Color(0xFF2A2118)  // Dark at bottom
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Top Row: Hijri Date and Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = "Date",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الثلاثاء 26 جمادى الثاني 1447 هـ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Mosque, // Placeholder for specific top-right icon
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Next Prayer Info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "المغرب", // Mock next prayer name logic
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = prayerTime?.maghrib ?: "--:--",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "10:20",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "موعد الصلاة القادمة بعد",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Daily Prayers Row
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DailyPrayerItem("الفجر", prayerTime?.fajr ?: "--:--", Icons.Default.WbCloudy, false)
                        DailyPrayerItem("الشروق", prayerTime?.sunrise ?: "--:--", Icons.Default.WbSunny, false) // Changed from WbSunrise
                        DailyPrayerItem("الظهر", prayerTime?.dhuhr ?: "--:--", Icons.Default.WbSunny, false)
                        DailyPrayerItem("العصر", prayerTime?.asr ?: "--:--", Icons.Default.WbCloudy, false) // Using Cloudy as placeholder
                        DailyPrayerItem("المغرب", prayerTime?.maghrib ?: "--:--", Icons.Default.Mosque, true)
                        DailyPrayerItem("العشاء", prayerTime?.isha ?: "--:--", Icons.Default.NightlightRound, false)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, // Shows as Next due to RTL
                        contentDescription = "View All",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            .padding(4.dp)
                    )

                    Text(
                        text = "إضغط لعرض جميع الصلوات",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DailyPrayerItem(name: String, time: String, icon: ImageVector, isActive: Boolean) {
    val alpha = if (isActive) 1f else 0.5f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (isActive) {
            Modifier
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(vertical = 4.dp, horizontal = 8.dp)
        } else {
            Modifier.padding(vertical = 4.dp, horizontal = 0.dp)
        }
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = alpha),
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Icon(
            imageVector = icon,
            contentDescription = name,
            tint = Color.White.copy(alpha = alpha),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = time,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = alpha),
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            fontSize = 10.sp
        )
    }
}

@Composable
fun ToolsSection(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "أدواتك",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.5f)),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "رؤية الكل",
                        style = MaterialTheme.typography.labelMedium,
                        color = GreenPrimaryLight
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft, // Points left in RTL
                        contentDescription = "View All",
                        tint = GreenPrimaryLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tools Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ToolCard(
                title = "القرآن الكريم",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                onClick = { navController.navigate(Screen.QuranIndex.route) },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            ToolCard(
                title = "الأذكار",
                icon = Icons.Default.SignLanguage,
                onClick = { navController.navigate(Screen.AdhkarList.route) },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            ToolCard(
                title = "التسبيح",
                icon = Icons.Default.SelfImprovement,
                onClick = { navController.navigate(Screen.Tasbih.route) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DrawerContent(navController: NavController, closeDrawer: () -> Unit) {
    val spacing = MaterialTheme.spacing

    Surface(
        color = GreenPrimaryLight,
        modifier = Modifier.fillMaxHeight().fillMaxWidth(0.81f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Profile Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = closeDrawer) {
                     Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close", tint = Color.White)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "مصطفى محمود",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "mostafaaita@gmail.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    // Avatar Placeholder
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(48.dp)
                    ) {
                        // Image goes here
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = GreenPrimaryLight, modifier = Modifier.padding(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Items
            Column(modifier = Modifier.weight(1f).padding(horizontal = 24.dp)) {
                DrawerMenuItem(icon = Icons.Default.Bookmark, title = "المفضلة") {
                    navController.navigate(Screen.Favorites.route)
                    closeDrawer()
                }
                DrawerMenuItem(icon = Icons.Default.Settings, title = "الإعدادات") { /* TODO */ }
                DrawerMenuItem(icon = Icons.Default.HeadsetMic, title = "تواصل معنا") { /* TODO */ }
                DrawerMenuItem(icon = Icons.Default.ThumbUp, title = "تقييم التطبيق") { /* TODO */ }
                DrawerMenuItem(icon = Icons.Default.HelpOutline, title = "من نحن؟") { /* TODO */ }
                DrawerMenuItem(icon = Icons.Default.Share, title = "شارك التطبيق") { /* TODO */ }
            }

            // Footer / Logout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Logout Action */ }
                ) {
                    Text(
                        text = "تسجيل الخروج",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "زاد مسلم",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "الدليل الإسلامي الشامل",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun ToolCard(title: String, icon: ImageVector, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f), // Make it a square
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Placeholder for illustrated icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HadithSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الأحاديث",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.5f)),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "رؤية الكل",
                        style = MaterialTheme.typography.labelMedium,
                        color = GreenPrimaryLight
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft, // Points left in RTL
                        contentDescription = "View All",
                        tint = GreenPrimaryLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hadith Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Top row with actions and source
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                        Text(
                            text = "الراوي : أبو سعيد الخدري",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "المحدث : ابن حبان",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Row {
                        IconButton(onClick = { /* Share */ }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = GreenPrimaryLight, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = { /* Copy */ }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GreenPrimaryLight, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = { /* Bookmark */ }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = "Bookmark", tint = GreenPrimaryLight, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "«ربنا ولك الحمد ملء السماوات والأرض، وملء ما شئت من شيء بعد...»",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

