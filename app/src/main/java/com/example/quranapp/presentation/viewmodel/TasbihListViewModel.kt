package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.TasbihItem
import com.example.quranapp.domain.repository.TasbihRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasbihListViewModel @Inject constructor(
    private val repository: TasbihRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.initializeDefaultTasbihsIfNeeded()
        }
    }

    val tasbihs: StateFlow<List<TasbihItem>> = repository.getAllTasbihs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalLifetimeCompletions: StateFlow<Int> = repository.getTotalLifetimeCompletions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun addCustomTasbih(phrase: String, targetCount: Int) {
        if (phrase.isBlank() || targetCount <= 0) return
        viewModelScope.launch {
            repository.addTasbih(phrase, targetCount)
        }
    }

    fun updateTasbih(item: TasbihItem) {
        viewModelScope.launch {
            repository.updateTasbih(item)
        }
    }

    fun deleteTasbih(item: TasbihItem) {
        if (item.isDefault) return // Don't allow deleting defaults unless user really wants to. Requirements didn't specify.
        viewModelScope.launch {
            repository.deleteTasbih(item)
        }
    }
}
