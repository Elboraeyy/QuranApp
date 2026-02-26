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
                // Refresh Button (Left)
                Surface(
                    shape = CircleShape,
                    color = GreenPrimaryLight.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, GreenPrimaryLight.copy(alpha = 0.3f)),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { viewModel.resetCount() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Count",
                            tint = GreenPrimaryLight
                        )
                    }
                }

                Text(
                    text = "التسبيح",
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

            Spacer(modifier = Modifier.height(16.dp))

            // Phrase Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant, // Adapts to light/dark
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gridMargin)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = phrase,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 32.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Arrow
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            IconButton(onClick = { /* Previous Phrase */ }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous", tint = MaterialTheme.colorScheme.onSecondary)
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
                            color = Color.Transparent,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.size(36.dp)
                        ) {
                            IconButton(onClick = { /* Next Phrase */ }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = MaterialTheme.colorScheme.secondary)
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
                            .size(90.dp)
                            .clickable { viewModel.incrementCount() }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                             // Background ring
                             CircularProgressIndicator(
                                 progress = { 1f },
                                 modifier = Modifier.fillMaxSize(),
                                 color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                 strokeWidth = 6.dp
                             )
                             
                             // Fill ring (Progress)
                             CircularProgressIndicator(
                                 progress = { currentCount.toFloat() / targetCount.toFloat() },
                                 modifier = Modifier.fillMaxSize(),
                                 color = MaterialTheme.colorScheme.primary,
                                 strokeWidth = 6.dp
                             )

                             // Inner Green Circle
                             Surface(
                                 shape = CircleShape,
                                 color = MaterialTheme.colorScheme.primary,
                                 modifier = Modifier
                                     .size(70.dp)
                                     .align(Alignment.Center)
                             ) {
                                  Column(
                                      horizontalAlignment = Alignment.CenterHorizontally,
                                      verticalArrangement = Arrangement.Center
                                  ) {
                                      Icon(
                                          imageVector = Icons.Default.TouchApp,
                                          contentDescription = "Tap",
                                          tint = MaterialTheme.colorScheme.onPrimary,
                                          modifier = Modifier.size(24.dp)
                                      )
                                      Text(
                                          text = "$currentCount/$targetCount",
                                          style = MaterialTheme.typography.titleMedium,
                                          fontWeight = FontWeight.Bold,
                                          color = MaterialTheme.colorScheme.onPrimary
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
