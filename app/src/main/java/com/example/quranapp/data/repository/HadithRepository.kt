package com.example.quranapp.data.repository

import android.content.Context
import com.example.quranapp.domain.model.Hadith
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

import com.example.quranapp.data.local.dao.HadithBookmarkDao
import com.example.quranapp.data.local.entity.HadithBookmarkEntity
import com.example.quranapp.domain.model.HadithBook
import com.example.quranapp.domain.model.HadithCategory
import kotlinx.coroutines.flow.Flow

@Singleton
class HadithRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bookmarkDao: HadithBookmarkDao
) {

    private data class HadithData(
        val books: List<HadithBook>,
        val categories: List<HadithCategory>,
        val hadiths: List<Hadith>
    )

    private var cachedData: HadithData? = null

    private suspend fun loadData(): HadithData? = withContext(Dispatchers.IO) {
        if (cachedData != null) return@withContext cachedData

        try {
            val inputStream = context.assets.open("hadiths.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<HadithData>() {}.type
            
            val gson = GsonBuilder()
                .registerTypeAdapter(String::class.java, JsonDeserializer { json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext? ->
                    if (json == null || json.isJsonNull) "" else json.asString
                })
                .create()
                
            cachedData = gson.fromJson(reader, type)
            reader.close()
            cachedData
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getAllHadiths(): List<Hadith> {
        return loadData()?.hadiths ?: emptyList()
    }

    suspend fun getBooks(): List<HadithBook> {
        return loadData()?.books ?: emptyList()
    }

    suspend fun getCategories(): List<HadithCategory> {
        return loadData()?.categories ?: emptyList()
    }

    suspend fun getRandomHadith(): Hadith? {
        val all = getAllHadiths()
        return if (all.isNotEmpty()) all.random() else null
    }

    // --- Bookmarks Logic ---

    fun getAllBookmarksFlow(): Flow<List<HadithBookmarkEntity>> {
        return bookmarkDao.getAllHadithBookmarks()
    }

    fun isHadithBookmarked(hadithId: Int): Flow<Boolean> {
        return bookmarkDao.isBookmarked(hadithId)
    }

    suspend fun toggleBookmark(hadith: Hadith) {
        val existing = bookmarkDao.getBookmarkById(hadith.id)
        if (existing != null) {
            bookmarkDao.deleteBookmark(existing)
        } else {
            bookmarkDao.insertBookmark(
                HadithBookmarkEntity(
                    hadithId = hadith.id,
                    bookId = hadith.bookId,
                    categoryId = hadith.categoryId
                )
            )
        }
    }
}
