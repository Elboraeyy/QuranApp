package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.presentation.ui.theme.spacing
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TafsirDetailScreen(
    navController: NavController,
    surahName: String = "سُورَةُ الْفَاتِحَةِ"
) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()
    
    val tabs = listOf(
        "المعنى الإجمالي",
        "تفسير الآيات",
        "الفوائد التربوية"
    )
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gridMargin, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Action Buttons (Left in LTR, Right visually if RTL mirror applied, wait, design has them on Left)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Bookmark
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.5f)),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = "Bookmark", tint = GreenPrimaryLight)
                        }
                    }
                    // Share
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.5f)),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = GreenPrimaryLight)
                        }
                    }
                }
                
                Text(
                    text = surahName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                // Back Button (Right in design)
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Scrollable Tabs
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = spacing.gridMargin),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(tabs.indices.toList()) { index ->
                    TafsirTabItem(
                        title = tabs[index],
                        isSelected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Scrollable Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = spacing.gridMargin, vertical = 8.dp)
            ) {
                // Ayah Text Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Bismillah (Green)
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            style = MaterialTheme.typography.titleLarge,
                            color = GreenPrimaryLight,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Quranic Text (Placeholder for Al-Fatihah)
                        Text(
                            text = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ﴿١﴾ الرَّحْمَٰنِ الرَّحِيمِ ﴿٢﴾ مَالِكِ يَوْمِ الدِّينِ ﴿٣﴾ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ﴿٤﴾ اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ﴿٥﴾ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ ﴿٦﴾",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp,
                            lineHeight = 32.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tafsir Content Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tabs[selectedTabIndex],
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook, // Placeholder for the open-book golden icon
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic Content based on selected tab
                when (selectedTabIndex) {
                    0 -> { // المعنى الإجمالي
                        Text(
                            text = "يخبر الله تعالى عباده بأن الحمد الكامل مستحق له وحده، ويرشدهم بما أخبر إلى أن يثنوا عليه، ويمجدوه، ويحمدوه بجميع المحامد التي لا يستحقها إلا هو، ذو الرحمة والملك، كما يرشدهم سبحانه إلى إفراده بالعبادة والاستعانة، وطلب الهداية منه وحده للطريق الواضحة التي لا اعوجاج فيها؛ طريق الذين أنعم الله عليهم، لا طريق اليهود المغضوب عليهم، ولا طريق النصارى الضالين.",
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 28.sp,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                    }
                    1 -> { // تفسير الآيات
                        // Mock data for Ayah Tafsir
                        val ayahTafsirs = listOf(
                            Pair("الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ﴿١﴾", "هذا خبَرٌ من الله عزَّ وجلَّ فيه حمد نفسه الكريمة، وفي ضمنه إرشاد لعباده بأن يحمدوه سبحانه وتعالى."),
                            Pair("الْحَمْدُ لِلَّهِ", "أي: جميع المحامد للمعبود تبارك وتعالى، لا يستحقها إلا هو وحده سبحانه، وهو حمد دائم ومستمر.")
                        )
                        
                        ayahTafsirs.forEach { (ayahText, tafsirText) ->
                            AyahTafsirItem(ayahText = ayahText, tafsirText = tafsirText)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    2 -> { // الفوائد التربوية
                        Text(
                            text = "من الفوائد التربوية لسورة الفاتحة:\n1. أهمية البدء باسم الله في كل عمل.\n2. الإقرار بربوبية الله الشاملة لجميع المخلوقات.\n3. تذكر يوم الحساب للاستعداد له بالعمل الصالح.\n4. إخلاص العبادة والاستعانة لله وحده.",
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 28.sp,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AyahTafsirItem(ayahText: String, tafsirText: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        // Ayah Box (Green tint)
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = GreenPrimaryLight.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.3f)),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(
                text = ayahText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        // Tafsir Text
        Text(
            text = tafsirText,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 28.sp,
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun TafsirTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface 
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    val borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            fontWeight = fontWeight,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
