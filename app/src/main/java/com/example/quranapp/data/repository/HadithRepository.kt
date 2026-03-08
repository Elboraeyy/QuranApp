package com.example.quranapp.data.repository

import android.content.Context
import com.example.quranapp.domain.model.Hadith
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HadithRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun getRandomHadith(): Hadith? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.assets.open("hadiths.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Hadith>>() {}.type
            val hadiths: List<Hadith> = Gson().fromJson(reader, type)
            reader.close()
            
            if (hadiths.isNotEmpty()) {
                hadiths.random()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
