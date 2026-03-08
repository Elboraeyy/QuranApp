package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.Bookmark
import com.example.quranapp.domain.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    val bookmarks: StateFlow<List<Bookmark>> = bookmarkRepository.getAllBookmarksFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addBookmark(surahNumber: Int, ayahNumber: Int, page: Int = 1, juz: Int? = null) {
        viewModelScope.launch {
            val bookmark = Bookmark(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                page = page,
                juz = juz
            )
            bookmarkRepository.addBookmark(bookmark)
        }
    }

    fun removeBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkRepository.deleteBookmark(bookmark)
        }
    }
}
