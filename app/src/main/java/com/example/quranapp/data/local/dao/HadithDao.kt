package com.example.quranapp.data.local.dao

import androidx.room.*
import com.example.quranapp.data.local.entity.HadithBookEntity
import com.example.quranapp.data.local.entity.HadithCategoryEntity
import com.example.quranapp.data.local.entity.HadithEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HadithDao {
    // --- Books ---
    @Query("SELECT * FROM hadith_books")
    suspend fun getAllBooks(): List<HadithBookEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<HadithBookEntity>)

    // --- Categories ---
    @Query("SELECT * FROM hadith_categories")
    suspend fun getAllCategories(): List<HadithCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<HadithCategoryEntity>)

    // --- Hadiths ---
    @Query("SELECT * FROM hadiths")
    suspend fun getAllHadiths(): List<HadithEntity>

    @Query("SELECT * FROM hadiths WHERE id = :id")
    suspend fun getHadithById(id: Int): HadithEntity?

    @Query("SELECT * FROM hadiths WHERE bookId = :bookId")
    suspend fun getHadithsByBook(bookId: Int): List<HadithEntity>

    @Query("SELECT * FROM hadiths WHERE categoryId = :categoryId")
    suspend fun getHadithsByCategory(categoryId: Int): List<HadithEntity>

    @Query("SELECT * FROM hadiths ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomHadith(): HadithEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadiths(hadiths: List<HadithEntity>)

    @Query("SELECT * FROM hadiths WHERE text LIKE '%' || :query || '%' OR narrator LIKE '%' || :query || '%'")
    suspend fun searchHadiths(query: String): List<HadithEntity>
}
