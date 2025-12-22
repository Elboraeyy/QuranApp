package com.example.quranapp.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quranapp.presentation.navigation.Screen
import com.example.quranapp.presentation.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerScreen(navController: NavController) {
    val viewModel: com.example.quranapp.presentation.viewmodel.AudioPlayerViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val duration by viewModel.durationMs.collectAsState()
    val position by viewModel.positionMs.collectAsState()
    val reciterName by viewModel.reciterName.collectAsState()
    val surahName by viewModel.surahName.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Audio Player") })
        },
        bottomBar = {
            BottomNavigationBar(navController, Screen.AudioPlayer.route)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Audio Player", style = MaterialTheme.typography.headlineMedium)
            if (reciterName.isNotEmpty() || surahName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "$reciterName - $surahName")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    scope.launch { viewModel.previous() }
                }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
                }
                FloatingActionButton(onClick = {
                    scope.launch {
                        if (isPlaying) viewModel.pause() else viewModel.playCurrent()
                    }
                }) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }
                IconButton(onClick = {
                    scope.launch { viewModel.next() }
                }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next")
                }
            }
            Slider(
                value = if (duration > 0) position.toFloat() / duration.toFloat() else 0f,
                onValueChange = { frac ->
                    val target = (frac * duration).toLong()
                    viewModel.seekTo(target)
                }
            )
            Text(text = "Duration: ${duration/1000}s | Position: ${position/1000}s")
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

