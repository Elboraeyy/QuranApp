package com.example.quranapp.data.local.dao

import androidx.room.*
import com.example.quranapp.data.local.entity.AdhkarCategoryEntity
import com.example.quranapp.data.local.entity.AdhkarItemEntity
import com.example.quranapp.data.local.entity.AdhkarProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdhkarDao {
    // --- Progress Queries ---
    @Query("SELECT * FROM adhkar_progress")
    fun getAllProgress(): Flow<List<AdhkarProgressEntity>>

    @Query("SELECT * FROM adhkar_progress")
    suspend fun getAllProgressList(): List<AdhkarProgressEntity>

    @Query("SELECT * FROM adhkar_progress WHERE adhkarId = :adhkarId")
    suspend fun getProgressForId(adhkarId: Int): AdhkarProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: AdhkarProgressEntity)

    @Query("UPDATE adhkar_progress SET currentCount = 0 WHERE lastUpdatedDate != :today")
    suspend fun resetOldProgress(today: String)

    @Query("DELETE FROM adhkar_progress")
    suspend fun clearAllProgress()

    // --- Content Queries ---
    @Query("SELECT * FROM adhkar_categories")
    fun getAllCategories(): Flow<List<AdhkarCategoryEntity>>

    @Query("SELECT * FROM adhkar_categories")
    suspend fun getAllCategoriesList(): List<AdhkarCategoryEntity>

    @Query("SELECT * FROM adhkar_categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): AdhkarCategoryEntity?

    @Query("SELECT * FROM adhkar_items WHERE categoryId = :categoryId")
    suspend fun getItemsByCategory(categoryId: Int): List<AdhkarItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<AdhkarCategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<AdhkarItemEntity>)
}
