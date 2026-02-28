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
        // Removed mock data insertion: initializeDatabase()
    }

    private fun initializeDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseInitializer(
                database.surahDao(),
                database.ayahDao()
            ).initializeDatabase()
        }
    }
}
