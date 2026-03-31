package com.example.quranapp.domain.repository

import com.example.quranapp.domain.model.Hadith
import com.example.quranapp.domain.model.HadithBook
import com.example.quranapp.domain.model.HadithCategory
import kotlinx.coroutines.flow.Flow

interface HadithRepository {
    suspend fun getAllBooks(): List<HadithBook>
    suspend fun getAllCategories(): List<HadithCategory>
    suspend fun getHadithsByBook(bookId: Int): List<Hadith>
    suspend fun getHadithsByCategory(categoryId: Int): List<Hadith>
    suspend fun getHadithById(id: Int): Hadith?
    suspend fun getRandomHadith(): Flow<Hadith?>
    suspend fun searchHadiths(query: String): List<Hadith>
    
    // Bookmark methods
    fun getAllBookmarksFlow(): Flow<List<com.example.quranapp.data.local.entity.HadithBookmarkEntity>>
    fun isHadithBookmarked(hadithId: Int): Flow<Boolean>
    suspend fun toggleBookmark(hadith: Hadith)
}
