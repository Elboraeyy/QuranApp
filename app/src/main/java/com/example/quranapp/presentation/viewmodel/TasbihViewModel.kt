package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.model.ZikrPhrase
import com.example.quranapp.domain.repository.TasbihRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasbihViewModel @Inject constructor(
    private val tasbihRepository: TasbihRepository
) : ViewModel() {

    private val _currentCount = MutableStateFlow(0)
    val currentCount: StateFlow<Int> = _currentCount.asStateFlow()

    private val _currentPhrase = MutableStateFlow<ZikrPhrase?>(null)
    val currentPhrase: StateFlow<ZikrPhrase?> = _currentPhrase.asStateFlow()

    private val _totalLifetimeCount = MutableStateFlow(0)
    val totalLifetimeCount: StateFlow<Int> = _totalLifetimeCount.asStateFlow()

    init {
        viewModelScope.launch {
            tasbihRepository.getTotalCountFlow().collect { total ->
                _totalLifetimeCount.value = total
            }
        }
        
        viewModelScope.launch {
            val phrases = tasbihRepository.getStandardPhrases()
            tasbihRepository.getSelectedPhraseIdFlow().collect { id ->
                _currentPhrase.value = phrases.find { it.id == id } ?: phrases.first()
            }
        }
    }

    fun incrementCount() {
        val count = _currentCount.value + 1
        _currentCount.value = count
        
        viewModelScope.launch {
            tasbihRepository.incrementTotalCount(1)
        }
    }

    fun resetCount() {
        _currentCount.value = 0
    }

    fun nextPhrase() {
        viewModelScope.launch {
            val phrases = tasbihRepository.getStandardPhrases()
            val current = _currentPhrase.value ?: return@launch
            val currentIndex = phrases.indexOfFirst { it.id == current.id }
            val nextIndex = (currentIndex + 1) % phrases.size
            
            _currentCount.value = 0
            tasbihRepository.saveSelectedPhraseId(phrases[nextIndex].id)
        }
    }

    fun previousPhrase() {
        viewModelScope.launch {
            val phrases = tasbihRepository.getStandardPhrases()
            val current = _currentPhrase.value ?: return@launch
            val currentIndex = phrases.indexOfFirst { it.id == current.id }
            val prevIndex = if (currentIndex - 1 < 0) phrases.size - 1 else currentIndex - 1
            
            _currentCount.value = 0
            tasbihRepository.saveSelectedPhraseId(phrases[prevIndex].id)
        }
    }
}
