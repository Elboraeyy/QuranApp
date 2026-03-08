package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.filled.FormatQuote
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
import androidx.compose.runtime.LaunchedEffect
import com.example.quranapp.domain.model.ThemeMode
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.Duration

import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.compose.ui.text.style.TextAlign
import com.example.quranapp.presentation.viewmodel.HadithViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    prayerViewModel: PrayerTimesViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    hadithViewModel: HadithViewModel = hiltViewModel()
) {
    val currentRoute = Screen.Home.route
    val spacing = MaterialTheme.spacing
    val scope = rememberCoroutineScope()

    val todayPrayerTime by prayerViewModel.today.collectAsState()
    val locationName by prayerViewModel.locationName.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()
    val currentHadith by hadithViewModel.currentHadith.collectAsState()

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

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                    HadithSection(hadith = currentHadith)
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp)) // Extra space for FAB and bottom bar overlap
                }
            }

            // Floating Bottom Navigation Bar
            BottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
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
        // Calculate next prayer
        var nextPrayerName = "--"
        var nextPrayerTime = "--:--"
        var remainingTime = "--:--"

        try {
            if (prayerTime != null && prayerTime.fajr.isNotEmpty() && !prayerTime.fajr.contains("--")) {
                val now = LocalTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")

                // Helper to clean and parse time
                fun parseTime(timeStr: String): LocalTime {
                    val cleanTime = timeStr.substringBefore(" ").trim()
                    return LocalTime.parse(cleanTime, formatter)
                }

                val prayers = listOf(
                    "الفجر" to parseTime(prayerTime.fajr),
                    "الشروق" to parseTime(prayerTime.sunrise),
                    "الظهر" to parseTime(prayerTime.dhuhr),
                    "العصر" to parseTime(prayerTime.asr),
                    "المغرب" to parseTime(prayerTime.maghrib),
                    "العشاء" to parseTime(prayerTime.isha)
                )

                val nextPrayer = prayers.firstOrNull { it.second.isAfter(now) }
                if (nextPrayer != null) {
                    nextPrayerName = nextPrayer.first
                    nextPrayerTime = nextPrayer.second.format(formatter)

                    val duration = Duration.between(now, nextPrayer.second)
                    val hours = duration.toHours()
                    val minutes = duration.toMinutesPart()
                    remainingTime = String.format("%02d:%02d", hours, minutes)
                } else {
                    // Next is tomorrow's Fajr
                    nextPrayerName = "الفجر"
                    nextPrayerTime = prayers[0].second.format(formatter)

                    // Add 24 hours for tomorrow's time to compute remaining time
                    val duration = Duration.between(now, prayers[0].second.plusHours(24))
                    val hours = duration.toHours()
                    val minutes = duration.toMinutesPart()
                    remainingTime = String.format("%02d:%02d", hours, minutes)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    // Beautiful elegant hero card
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp, // Soft elevation
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp), // More breathing room
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // "Next Prayer" subtitle
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GreenPrimaryLight.copy(alpha = 0.08f)
            ) {
                Text(
                    text = "الصلاة القادمة",
                    style = MaterialTheme.typography.labelMedium,
                    color = GreenPrimaryLight,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The Main Prayer Time Display (Huge, thin elegant font)
            Text(
                text = nextPrayerName,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 2.sp
            )

            // Sub-time
            Text(
                text = "باقي $remainingTime",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.8f),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Grid of prayer times at the bottom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PrayerTimeColumn("الفجر", prayerTime?.fajr ?: "00:00", isNext = nextPrayerName == "الفجر")
                PrayerTimeColumn("الظهر", prayerTime?.dhuhr ?: "00:00", isNext = nextPrayerName == "الظهر")
                PrayerTimeColumn("العصر", prayerTime?.asr ?: "00:00", isNext = nextPrayerName == "العصر")
                PrayerTimeColumn("المغرب", prayerTime?.maghrib ?: "00:00", isNext = nextPrayerName == "المغرب")
                PrayerTimeColumn("العشاء", prayerTime?.isha ?: "00:00", isNext = nextPrayerName == "العشاء")
            }
        }
    }
    }
}

@Composable
fun PrayerTimeColumn(name: String, time: String, isNext: Boolean = false) {
    val contentColor = if (isNext) GreenPrimaryLight else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        if (isNext) {
            // Elegant dot indicator for the next prayer
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(contentColor, CircleShape)
                    .offset(y = (-8).dp)
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = fontWeight
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isNext) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            fontWeight = fontWeight
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

        // Elegant horizontal list of tools
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            item {
                ToolCard(
                title = "القرآن الكريم",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    onClick = { navController.navigate(Screen.QuranIndex.route) },
                    modifier = Modifier.width(110.dp)
                )
            }
            item {
                ToolCard(
                    title = "الصلاة",
                    icon = Icons.Default.Mosque,
                    onClick = { navController.navigate(Screen.PrayerTimes.route) },
                    modifier = Modifier.width(110.dp)
                )
            }
            item {
                ToolCard(
                    title = "القبلة",
                    icon = Icons.Default.Explore,
                    onClick = { navController.navigate(Screen.Qibla.route) },
                    modifier = Modifier.width(110.dp)
                )
            }
            item {
                ToolCard(
                    title = "الأذكار",
                    icon = Icons.Default.LibraryBooks,
                    onClick = { navController.navigate(Screen.AdhkarList.route) },
                    modifier = Modifier.width(110.dp)
                )
            }
            item {
                ToolCard(
                    title = "التسبيح",
                    icon = Icons.Default.Favorite,
                    onClick = { navController.navigate(Screen.Tasbih.route) },
                    modifier = Modifier.width(110.dp)
                )
            }
            item {
                ToolCard(
                    title = "الحديث",
                    icon = Icons.Default.MenuBook, // Fallback icon for Hadith
                    onClick = { /* Navigate to Hadith Screen when implemented */ },
                    modifier = Modifier.width(110.dp)
                )
            }
        }
    }
}

@Composable
fun ToolCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp), // Soft round corners
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp, // Subtle lift
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
        modifier = modifier
            .aspectRatio(0.9f) // Slightly taller than wide for elegance
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon surrounded by an elegant soft circle
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.size(56.dp) // Generous icon container
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = GreenPrimaryLight,
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
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
                val context = LocalContext.current
                DrawerMenuItem(icon = Icons.Default.Bookmark, title = "المفضلة") {
                    navController.navigate(Screen.Favorites.route)
                    closeDrawer()
                }
                DrawerMenuItem(icon = Icons.Default.Settings, title = "الإعدادات") {
                    navController.navigate(Screen.Settings.route)
                    closeDrawer()
                }
                DrawerMenuItem(icon = Icons.Default.HeadsetMic, title = "تواصل معنا") {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = android.net.Uri.parse("mailto:support@zadmuslim.com")
                        putExtra(Intent.EXTRA_SUBJECT, "تطبيق زاد مسلم - تواصل")
                    }
                    context.startActivity(Intent.createChooser(intent, "اختر تطبيق البريد"))
                    closeDrawer()
                }
                DrawerMenuItem(icon = Icons.Default.ThumbUp, title = "تقييم التطبيق") {
                    // In a real app, link to play store
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse("market://details?id=${context.packageName}")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
                    }
                    closeDrawer()
                }
                DrawerMenuItem(icon = Icons.Default.HelpOutline, title = "عن التطبيق") {
                    navController.navigate(Screen.About.route)
                    closeDrawer()
                }
                DrawerMenuItem(icon = Icons.Default.Share, title = "شارك التطبيق") {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "تطبيق زاد مسلم")
                        putExtra(Intent.EXTRA_TEXT, "حمل تطبيق زاد مسلم دليلك الشامل لكل ما يحتاجه المسلم: https://play.google.com/store/apps/details?id=${context.packageName}")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق"))
                    closeDrawer()
                }
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

// Duplicated ToolCard removed from here

@Composable
fun HadithSection(hadith: com.example.quranapp.domain.model.Hadith?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "حديث اليوم",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hadith Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Top row with quote icon and share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = Color(0xFFC9A24D).copy(alpha = 0.5f), // Gold Quote Icon
                        modifier = Modifier.size(40.dp)
                    )

                    val context = LocalContext.current
                    val shareText = "${hadith?.text}\n- ${hadith?.narrator} (${hadith?.source})"

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFC9A24D).copy(alpha = 0.1f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        IconButton(onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "مشاركة الحديث"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = GreenPrimaryLight, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // The Hadith Text
                Text(
                    text = hadith?.text ?: "جاري التحميل...",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = com.example.quranapp.presentation.ui.theme.ScheherazadeNew,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Source Info
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "الراوي : ${hadith?.narrator ?: "غير متوفر"}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimaryLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "المصدر : ${hadith?.source ?: ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

