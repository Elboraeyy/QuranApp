package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.R
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import com.example.quranapp.presentation.ui.theme.spacing

@Composable
fun QiblaScreen(navController: NavController) {
    val viewModel: com.example.quranapp.presentation.viewmodel.QiblaViewModel = hiltViewModel()
    val direction by viewModel.direction.collectAsState()
    val qiblaAngle by viewModel.qiblaAngle.collectAsState()
    val distance by viewModel.distance.collectAsState()
    val spacing = MaterialTheme.spacing

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = spacing.gridMargin),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            QiblaHeader(onBack = { navController.popBackStack() }, onRefresh = { /* Refresh Location */ })
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Map Card
            QiblaMapCard(distance = distance)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Instruction
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "برجاء قم بتدوير الهاتف للشمال قليلاً",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFC04C36), // A reddish warning/instruction color
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ScreenRotation,
                    contentDescription = "Rotate",
                    tint = Color(0xFFC04C36),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Compass
            QiblaCompass(direction = direction, qiblaAngle = qiblaAngle)
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Angle Indicator
            Surface(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.3f)),
                color = GreenPrimaryLight.copy(alpha = 0.1f),
                modifier = Modifier.padding(bottom = 32.dp) // Bottom padding
            ) {
                Text(
                    text = "زاوية القبلة ${qiblaAngle.toInt()}°",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun QiblaHeader(onBack: () -> Unit, onRefresh: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button (Right in RTL)
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.size(48.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = "القبلة",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Refresh Button (Left in RTL)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = GreenPrimaryLight.copy(alpha = 0.1f),
            modifier = Modifier.size(48.dp)
        ) {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Calibration",
                    tint = GreenPrimaryLight
                )
            }
        }
    }
}

@Composable
fun QiblaMapCard(distance: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Map Background Placeholder (Gradient for now)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFCDE2E0), // Light map sea color
                                Color(0xFFEFE8DA)  // Light map land color
                            )
                        )
                    )
            )

            // Fullscreen Icon
            IconButton(
                onClick = { /* Open full map */ },
                modifier = Modifier
                    .align(Alignment.TopEnd) // Rendered on Left in RTL visually, but let's stick to logical alignment
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Fullscreen",
                    tint = Color.Black,
                    modifier = Modifier.padding(2.dp)
                )
            }

            // Bottom Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // Location Text
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "المسافة من مكة ${distance}كم",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Kaaba miniature icon
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.Black, RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(MaterialTheme.colorScheme.secondary) // Gold band
                                .align(Alignment.TopCenter)
                                .padding(top = 4.dp)
                        )
                    }
                }
                
                // Separator line
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(20.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "مصر",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Simplified Flag Placeholder
                    Box(
                        modifier = Modifier
                            .size(width = 24.dp, height = 16.dp)
                            .clip(RoundedCornerShape(2.dp))
                    ) {
                        Column {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Red))
                            Box(modifier = Modifier.weight(1f).fillMaxWidth().background(MaterialTheme.colorScheme.onSurface))
                            Box(modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Black))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QiblaCompass(direction: Float, qiblaAngle: Float) {
    Box(
        modifier = Modifier
            .size(300.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer rings
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(8.dp, Color(0xFFE0E0E0), CircleShape)
        )
        Box(
            modifier = Modifier
                .fillMaxSize(0.95f)
                .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape) // Goldish trim
        )

        // Rotate the inner compass based on direction
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(-direction),
            contentAlignment = Alignment.Center
        ) {
            // Compass background (Placeholder for actual asset)
            Box(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .background(Color(0xFFF5F5F5), CircleShape)
                    .border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
            ) {
                // Direction letters (N, E, S, W) - Just N for reference
                Text(
                    text = "N",
                    modifier = Modifier.align(Alignment.TopCenter).padding(8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            // Compass Needle (Red pointing North, white pointing South)
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.Center
            ) {
                // Red Needle
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(16.dp)
                        .background(Color(0xFFC04C36))
                )
                // Center Pin
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF333333), CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                )
                // White/Grey Needle
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(16.dp)
                        .background(Color(0xFFE0E0E0))
                )
            }
        }

        // Fixed Kaaba indicator at the top (or specific angle if logic implemented)
        // For visual representation of the design, we place it at the top.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(qiblaAngle - direction) // Kaaba relative to current direction
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black, RoundedCornerShape(4.dp))
                    .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(MaterialTheme.colorScheme.secondary) // Gold band
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                )
            }
            // Small pointer triangle
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 6.dp)
                    .size(12.dp)
                    .rotate(45f)
                    .background(MaterialTheme.colorScheme.secondary)
            )
        }
    }
}

