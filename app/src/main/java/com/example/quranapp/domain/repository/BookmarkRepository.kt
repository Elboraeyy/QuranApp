package com.example.quranapp.domain.repository

import com.example.quranapp.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    suspend fun addBookmark(bookmark: Bookmark): Long
    suspend fun deleteBookmark(bookmark: Bookmark)
    suspend fun deleteBookmarkById(id: Long)
    suspend fun getBookmark(surahNumber: Int, ayahNumber: Int): Bookmark?
    suspend fun getAllBookmarks(): List<Bookmark>
    fun getAllBookmarksFlow(): Flow<List<Bookmark>>
    suspend fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean
}

