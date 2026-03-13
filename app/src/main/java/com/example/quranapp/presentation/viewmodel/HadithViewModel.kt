package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.data.repository.HadithRepository
import com.example.quranapp.domain.model.Hadith
import com.example.quranapp.domain.model.HadithBook
import com.example.quranapp.domain.model.HadithCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HadithViewModel @Inject constructor(
    private val repository: HadithRepository
) : ViewModel() {

    private val _currentHadith = MutableStateFlow<Hadith?>(null)
    val currentHadith: StateFlow<Hadith?> = _currentHadith.asStateFlow()

    private val _allHadiths = MutableStateFlow<List<Hadith>>(emptyList())
    val allHadiths: StateFlow<List<Hadith>> = _allHadiths.asStateFlow()
    
    private val _allBooks = MutableStateFlow<List<HadithBook>>(emptyList())
    val allBooks: StateFlow<List<HadithBook>> = _allBooks.asStateFlow()

    private val _allCategories = MutableStateFlow<List<HadithCategory>>(emptyList())
    val allCategories: StateFlow<List<HadithCategory>> = _allCategories.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Int?>(1) // Default expanded
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    private val _selectedBookId = MutableStateFlow<Int?>(null)
    val selectedBookId: StateFlow<Int?> = _selectedBookId.asStateFlow()

    val filteredHadiths: StateFlow<List<Hadith>> = combine(
        _allHadiths,
        _searchQuery,
        _selectedCategoryId,
        _selectedBookId
    ) { hadiths, query, categoryId, bookId ->
        var filteredList = hadiths
        if (query.isNotBlank()) {
            filteredList = filteredList.filter {
                it.text.contains(query, ignoreCase = true) || 
                it.narrator.contains(query, ignoreCase = true) ||
                it.source.contains(query, ignoreCase = true)
            }
        } else if (categoryId != null) {
            filteredList = filteredList.filter { it.categoryId == categoryId }
        } else if (bookId != null) {
            filteredList = filteredList.filter { it.bookId == bookId }
        }
        filteredList
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val hadiths = repository.getAllHadiths()
            val books = repository.getBooks()
            val categories = repository.getCategories()
            
            _allHadiths.value = hadiths
            _allBooks.value = books
            _allCategories.value = categories
            
            if (hadiths.isNotEmpty()) {
                _currentHadith.value = hadiths.random()
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            _selectedCategoryId.value = null // Clear category filter when searching
            _selectedBookId.value = null // Clear book filter
        }
    }

    fun onCategorySelected(categoryId: Int?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
        if (categoryId != null) {
            _searchQuery.value = "" // Clear search when picking a category
            _selectedBookId.value = null // Clear book filter
        }
    }

    fun onBookSelected(bookId: Int?) {
        _selectedBookId.value = if (_selectedBookId.value == bookId) null else bookId
        if (bookId != null) {
            _searchQuery.value = ""
            _selectedCategoryId.value = null
        }
    }

    // --- Bookmarks Logic ---

    fun isBookmarked(hadithId: Int) = repository.isHadithBookmarked(hadithId)

    fun toggleBookmark(hadith: Hadith) {
        viewModelScope.launch {
            repository.toggleBookmark(hadith)
        }
    }
    
    // Read the bookmarked items and map them to their corresponding Hadith objects
    val bookmarkedHadiths: StateFlow<List<Hadith>> = combine(
        repository.getAllBookmarksFlow(),
        _allHadiths
    ) { bookmarkEntities, hadiths ->
        val bookmarkIds = bookmarkEntities.map { it.hadithId }
        hadiths.filter { it.id in bookmarkIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
