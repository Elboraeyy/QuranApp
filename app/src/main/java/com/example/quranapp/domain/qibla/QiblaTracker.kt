package com.example.quranapp.domain.qibla

import kotlinx.coroutines.flow.Flow

interface QiblaTracker {
    fun startTracking(): Flow<Float>
    fun stopTracking()
}
