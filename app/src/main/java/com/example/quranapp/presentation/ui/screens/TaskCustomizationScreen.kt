package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.quranapp.data.local.entity.TaskCategory
import com.example.quranapp.presentation.ui.theme.*
import com.example.quranapp.presentation.viewmodel.ReligiousTasksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCustomizationScreen(
    navController: NavController,
    viewModel: ReligiousTasksViewModel = hiltViewModel()
) {
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("تخصيص المهام", fontWeight = FontWeight.Bold) },
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
                .background(Brush.verticalGradient(listOf(GreenPrimaryLight, BackgroundLight)))
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("ابحث عن مهمة...", color = Color.White.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldSecondaryLight,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("اختر المهام التي تريد متابعتها", color = Color.White, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // This would ideally come from another list of "All Available Tasks"
                    // For now, we show categories
                    items(TaskCategory.entries) { category ->
                        CategorySwitchCard(category)
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldSecondaryLight),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("حفظ الإعدادات", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CategorySwitchCard(category: TaskCategory) {
    var enabled by remember { mutableStateOf(true) }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.9f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = when(category) {
                        TaskCategory.PRAYER -> "الصلاة"
                        TaskCategory.QURAN -> "القرآن"
                        TaskCategory.ADHKAR -> "الأذكار"
                        TaskCategory.FASTING -> "الصيام"
                        TaskCategory.CHARITY -> "الصدقة"
                        TaskCategory.OTHER -> "أخرى"
                    },
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
                Text("تفعيل جميع مهام هذا القسم", fontSize = 12.sp, color = TextPrimaryLight.copy(alpha = 0.6f))
            }
            Switch(
                checked = enabled,
                onCheckedChange = { enabled = it },
                colors = SwitchDefaults.colors(checkedThumbColor = GreenPrimaryLight, checkedTrackColor = GreenPrimaryLight.copy(alpha = 0.3f))
            )
        }
    }
}
