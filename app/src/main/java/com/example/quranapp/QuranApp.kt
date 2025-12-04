package com.example.quranapp

import android.app.Application
import com.example.quranapp.data.local.database.DatabaseInitializer
import com.example.quranapp.data.local.database.QuranDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class QuranApp : Application() {

    @Inject
    lateinit var database: QuranDatabase

    override fun onCreate() {
        super.onCreate()
        
        // Initialize database
        CoroutineScope(Dispatchers.IO).launch {
            val initializer = DatabaseInitializer(
                database.surahDao(),
                database.ayahDao()
            )
            initializer.initializeDatabase()
        }
    }
}
