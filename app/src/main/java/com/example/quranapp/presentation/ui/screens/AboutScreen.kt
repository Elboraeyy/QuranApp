package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight

@Composable
fun AboutScreen(navController: NavController) {
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
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Invisible placeholder to center title
                Spacer(modifier = Modifier.size(44.dp))

                Text(
                    text = "عن التطبيق",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Back Button (Right in RTL)
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GreenPrimaryLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Details Card
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Logo Placeholder
                    Surface(
                        shape = CircleShape,
                        color = GreenPrimaryLight.copy(alpha = 0.1f),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Text(
                            text = "زاد",
                            style = MaterialTheme.typography.headlineLarge,
                            fontFamily = com.example.quranapp.presentation.ui.theme.ScheherazadeNew,
                            color = GreenPrimaryLight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentSize()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "زاد مسلم",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "الإصدار 1.0.0",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "تطبيق إسلامي شامل يحتوي على القرآن الكريم، الأذكار، مواقيت الصلاة، واتجاه القبلة بتصميم راقٍ وعصري لخدمة كل مسلم.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

