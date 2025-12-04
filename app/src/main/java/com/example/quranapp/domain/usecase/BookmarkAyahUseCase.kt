package com.example.quranapp.domain.usecase

import com.example.quranapp.domain.model.Bookmark
import com.example.quranapp.domain.repository.BookmarkRepository
import javax.inject.Inject

class BookmarkAyahUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(bookmark: Bookmark): Long {
        return repository.addBookmark(bookmark)
    }
}

class RemoveBookmarkUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(bookmark: Bookmark) {
        repository.deleteBookmark(bookmark)
    }
}

class IsBookmarkedUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(surahNumber: Int, ayahNumber: Int): Boolean {
        return repository.isBookmarked(surahNumber, ayahNumber)
    }
}

