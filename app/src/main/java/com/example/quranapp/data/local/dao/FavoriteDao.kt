package com.example.quranapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quranapp.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    
    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    suspend fun getAllFavoritesList(): List<FavoriteEntity>
    
    @Query("SELECT * FROM favorites WHERE type = :type ORDER BY createdAt DESC")
    suspend fun getFavoritesByType(type: String): List<FavoriteEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity): Long
    
    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)
    
    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavoriteById(id: Long)
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE type = :type AND surahNumber = :surahNumber AND ayahNumber = :ayahNumber AND (:reciterId IS NULL OR reciterId = :reciterId) AND (:tafsirId IS NULL OR tafsirId = :tafsirId))")
    suspend fun isFavorite(type: String, surahNumber: Int?, ayahNumber: Int?, reciterId: String?, tafsirId: String?): Boolean
}

