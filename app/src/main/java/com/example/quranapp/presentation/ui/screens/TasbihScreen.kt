package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranapp.R
import com.example.quranapp.presentation.ui.theme.spacing
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quranapp.presentation.viewmodel.TasbihViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(
    navController: NavController,
    viewModel: TasbihViewModel = hiltViewModel()
) {
    val spacing = MaterialTheme.spacing
    
    val currentCount by viewModel.currentCount.collectAsState()
    val targetCount = 33
    val phrase = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ"

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
                // Back Button (Right in Arabic)
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Standard Back icon
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = "التسبيح",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Refresh Button (Left in Arabic)
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFC9A24D).copy(alpha = 0.1f),
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(onClick = { viewModel.resetCount() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Count",
                            tint = GreenPrimaryLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phrase Card
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gridMargin)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = phrase,
                        style = MaterialTheme.typography.headlineLarge,
                        fontFamily = com.example.quranapp.presentation.ui.theme.ScheherazadeNew,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = GreenPrimaryLight,
                        lineHeight = 48.sp,
                        fontSize = 32.sp
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Arrow
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            IconButton(onClick = { /* Previous Phrase */ }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous", tint = GreenPrimaryLight)
                            }
                        }
                        
                        // Count Target
                        Row(verticalAlignment = Alignment.CenterVertically) {
                           Text(
                               text = "$targetCount مره",
                               style = MaterialTheme.typography.titleMedium,
                               fontWeight = FontWeight.Bold,
                               color = MaterialTheme.colorScheme.onSurface
                           )
                           Spacer(modifier = Modifier.width(8.dp))
                           Icon(
                               imageVector = Icons.Default.TouchApp,
                               contentDescription = null,
                               tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                               modifier = Modifier.size(18.dp)
                           )
                        }
                        
                        // Right Arrow
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFC9A24D).copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            IconButton(onClick = { /* Next Phrase */ }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = GreenPrimaryLight)
                            }
                        }
                    }
                }
            }

            // Central Illustration and floating counter
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = spacing.gridMargin)
            ) {
                // Placeholder for the hand holding rosary illustration
                // In reality, this would be an Image composable loading a resource
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // For now, represent it with a shape or just text indicating the image goes here
                    // To avoid a broken UI in the meantime, I'll put a subtle placeholder
                    Text("Hand/Rosary Illustration Placeholder", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    
                    // IF image exists in drawn assets:
                    // Image(
                    //    painter = painterResource(id = R.drawable.ic_rosary_hand),
                    //    contentDescription = "Rosary",
                    //    modifier = Modifier.fillMaxSize(),
                    //    contentScale = ContentScale.Fit
                    // )
                }
                
                // Floating Counter Button (Bottom Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 32.dp, end = 16.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { viewModel.incrementCount() }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                             // Background ring
                             CircularProgressIndicator(
                                 progress = { 1f },
                                 modifier = Modifier.fillMaxSize(0.9f),
                                 color = Color(0xFFC9A24D).copy(alpha = 0.2f),
                                 strokeWidth = 6.dp
                             )
                             
                             // Fill ring (Progress)
                             CircularProgressIndicator(
                                 progress = { currentCount.toFloat() / targetCount.toFloat() },
                                 modifier = Modifier.fillMaxSize(0.9f),
                                 color = Color(0xFFC9A24D),
                                 strokeWidth = 6.dp
                             )

                             // Inner Green Circle
                             Surface(
                                 shape = CircleShape,
                                 color = GreenPrimaryLight,
                                 modifier = Modifier
                                     .size(80.dp)
                                     .align(Alignment.Center)
                             ) {
                                  Column(
                                      horizontalAlignment = Alignment.CenterHorizontally,
                                      verticalArrangement = Arrangement.Center
                                  ) {
                                      Icon(
                                          imageVector = Icons.Default.TouchApp,
                                          contentDescription = "Tap",
                                          tint = Color.White,
                                          modifier = Modifier.size(24.dp)
                                      )
                                      Text(
                                          text = "$currentCount/$targetCount",
                                          style = MaterialTheme.typography.titleMedium,
                                          fontWeight = FontWeight.Bold,
                                          color = Color.White
                                      )
                                  }
                             }
                        }
                    }
                }
            }
        }
    }
}
