package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.AdhkarCategory
import com.example.quranapp.domain.repository.AdhkarRepository
import com.example.quranapp.domain.repository.FavoriteRepository
import com.example.quranapp.domain.model.Favorite
import com.example.quranapp.domain.model.FavoriteType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdhkarViewModel @Inject constructor(
    private val repository: AdhkarRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<AdhkarCategory>>(emptyList())
    val categories: StateFlow<List<AdhkarCategory>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getCategories().collect {
                _categories.value = it
                _isLoading.value = false
            }
        }
    }

    fun updateAdhkarCount(adhkarId: Int, newCount: Int) {
        viewModelScope.launch {
            repository.updateAdhkarCount(adhkarId, newCount)
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
        }
    }
    
    fun getCategoryById(id: Int): Flow<AdhkarCategory?> {
        return categories.map { list -> list.find { it.id == id } }
    }

    fun observeIsFavorite(adhkarId: Int): Flow<Boolean> {
        return favoriteRepository.observeIsAdhkarFavorite(adhkarId)
    }

    fun toggleFavorite(adhkarId: Int) {
        viewModelScope.launch {
            val existing = favoriteRepository.getFavoriteByAdhkarId(adhkarId)
            if (existing != null) {
                favoriteRepository.deleteFavorite(existing)
            } else {
                val newFavorite = Favorite(
                    type = FavoriteType.ADHKAR,
                    adhkarId = adhkarId
                )
                favoriteRepository.addFavorite(newFavorite)
            }
        }
    }
}
