package com.example.quranapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quranapp.data.local.entity.HadithBookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HadithBookmarkDao {

    @Query("SELECT * FROM hadith_bookmarks ORDER BY timestamp DESC")
    fun getAllHadithBookmarks(): Flow<List<HadithBookmarkEntity>>

    @Query("SELECT * FROM hadith_bookmarks WHERE hadithId = :id LIMIT 1")
    suspend fun getBookmarkById(id: Int): HadithBookmarkEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM hadith_bookmarks WHERE hadithId = :id LIMIT 1)")
    fun isBookmarked(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: HadithBookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: HadithBookmarkEntity)
    
    @Query("DELETE FROM hadith_bookmarks WHERE hadithId = :id")
    suspend fun deleteBookmarkById(id: Int)
}
