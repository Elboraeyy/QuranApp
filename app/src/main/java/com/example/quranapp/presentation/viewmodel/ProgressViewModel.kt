package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val repository: ProgressRepository
) : ViewModel() {
    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _pagesToday = MutableStateFlow(0)
    val pagesToday: StateFlow<Int> = _pagesToday.asStateFlow()

    init {
        viewModelScope.launch {
            _streak.value = repository.getReadingStreak().currentStreak
            _pagesToday.value = repository.getTodayProgress()?.pagesRead ?: 0
        }
    }
}
