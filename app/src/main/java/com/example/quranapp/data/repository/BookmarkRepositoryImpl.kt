package com.example.quranapp.data.repository

import com.example.quranapp.data.local.dao.BookmarkDao
import com.example.quranapp.data.local.mapper.toDomain
import com.example.quranapp.data.local.mapper.toEntity
import com.example.quranapp.domain.model.Bookmark
import com.example.quranapp.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {
    
    override suspend fun addBookmark(bookmark: Bookmark): Long {
        return bookmarkDao.insertBookmark(bookmark.toEntity())
    }
    
    override suspend fun deleteBookmark(bookmark: Bookmark) {
        bookmarkDao.deleteBookmark(bookmark.toEntity())
    }
    
    override suspend fun deleteBookmarkById(id: Long) {
        bookmarkDao.deleteBookmarkById(id)
    }
    
    override suspend fun getBookmark(surahNumber: Int, ayahNumber: Int): Bookmark? {
        return bookmarkDao.getBookmark(surahNumber, ayahNumber)?.toDomain()
    }
    
    override suspend fun getAllBookmarks(): List<Bookmark> {
        return bookmarkDao.getAllBookmarksList().map { it.toDomain() }
    }
    
    override fun getAllBookmarksFlow(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks().map { bookmarks -> bookmarks.map { it.toDomain() } }
    }
    
    override suspend fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        return bookmarkDao.isBookmarked(surahNumber, ayahNumber)
    }
}

