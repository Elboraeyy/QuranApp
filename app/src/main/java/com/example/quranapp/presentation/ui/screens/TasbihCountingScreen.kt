package com.example.quranapp.presentation.ui.screens

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.quranapp.presentation.ui.theme.GreenPrimaryLight
import com.example.quranapp.presentation.viewmodel.TasbihCountingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihCountingScreen(
    navController: NavController,
    viewModel: TasbihCountingViewModel = hiltViewModel()
) {
    val tasbihItem by viewModel.tasbihItem.collectAsState()
    val currentCount by viewModel.currentCount.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Animation scale for the big button
    val scale = remember { Animatable(1f) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (tasbihItem == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimaryLight)
            }
        } else {
            val item = tasbihItem!!
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Header (Back + Title + Reset)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(44.dp),
                        shadowElevation = 2.dp
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Right in RTL
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = "التسبيح والذكر",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFC9A24D).copy(alpha = 0.1f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        IconButton(onClick = { viewModel.resetSessionCount() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Count",
                                tint = GreenPrimaryLight
                            )
                        }
                    }
                }

                // Phrase Card View
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
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.phrase,
                            style = MaterialTheme.typography.headlineLarge,
                            fontFamily = com.example.quranapp.presentation.ui.theme.ScheherazadeNew,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = GreenPrimaryLight,
                            lineHeight = 48.sp,
                            fontSize = 32.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Details Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "نطاق الذكر",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = item.targetCount.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "تم الختم بالكامل",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = item.totalCompletions.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC9A24D)
                        )
                    }
                }

                // The Big Modern Circular Tap Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val progress = if (item.targetCount > 0) currentCount.toFloat() / item.targetCount else 0f

                    // Outer massive circle
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .scale(scale.value)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        GreenPrimaryLight.copy(alpha = 0.8f),
                                        GreenPrimaryLight.copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                // Haptic Feedback
                                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                                    vibratorManager.defaultVibrator
                                } else {
                                    @Suppress("DEPRECATION")
                                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    // Make completion buzz strong, regular tap light
                                    if (currentCount + 1 == item.targetCount) {
                                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                                    } else {
                                        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                                    }
                                } else {
                                    @Suppress("DEPRECATION")
                                    vibrator.vibrate(30)
                                }

                                viewModel.incrementCount()

                                // Animation bounce
                                coroutineScope.launch {
                                    scale.animateTo(0.95f, animationSpec = tween(100))
                                    scale.animateTo(1f, animationSpec = tween(100))
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner solid circle
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 16.dp,
                            modifier = Modifier.size(200.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                // Progress Ring Background
                                CircularProgressIndicator(
                                    progress = { 1f },
                                    modifier = Modifier.fillMaxSize(0.9f),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                    strokeWidth = 8.dp
                                )
                                
                                // Progress Ring Foreground
                                CircularProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxSize(0.9f),
                                    color = Color(0xFFC9A24D),
                                    strokeWidth = 8.dp
                                )
                                
                                // Text Count
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = currentCount.toString(),
                                        style = MaterialTheme.typography.displayLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GreenPrimaryLight
                                    )
                                    if (item.targetCount > 0) {
                                        Text(
                                            text = "المتبقي: ${item.targetCount - currentCount}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Undo Button at the bottom left
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 16.dp)
                            .size(56.dp),
                        shadowElevation = 8.dp
                    ) {
                        IconButton(onClick = { viewModel.decrementCount() }) {
                            Icon(
                                imageVector = Icons.Default.Undo,
                                contentDescription = "Undo Count",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
