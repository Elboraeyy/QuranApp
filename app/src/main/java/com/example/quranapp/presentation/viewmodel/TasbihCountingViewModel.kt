package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.TasbihItem
import com.example.quranapp.domain.repository.TasbihRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasbihCountingViewModel @Inject constructor(
    private val repository: TasbihRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tasbihId: Int = checkNotNull(savedStateHandle["tasbihId"])

    private val _tasbihItem = MutableStateFlow<TasbihItem?>(null)
    val tasbihItem: StateFlow<TasbihItem?> = _tasbihItem.asStateFlow()

    private val _currentCount = MutableStateFlow(0)
    val currentCount: StateFlow<Int> = _currentCount.asStateFlow()

    init {
        loadTasbih()
    }

    private fun loadTasbih() {
        viewModelScope.launch {
            _tasbihItem.value = repository.getTasbihById(tasbihId)
        }
    }

    fun incrementCount() {
        val count = _currentCount.value + 1
        val target = _tasbihItem.value?.targetCount ?: 33
        
        _currentCount.value = count
        
        if (count == target) {
            viewModelScope.launch {
                repository.incrementTasbihCompletions(tasbihId)
                // Reload to update total completions in UI
                loadTasbih()
                
                // Small delay so user can see it hit the target before resetting
                kotlinx.coroutines.delay(400)
                _currentCount.value = 0
            }
        }
    }

    fun resetSessionCount() {
        _currentCount.value = 0
    }

    fun decrementCount() {
        val current = _currentCount.value
        if (current > 0) {
            _currentCount.value = current - 1
        }
    }
}
