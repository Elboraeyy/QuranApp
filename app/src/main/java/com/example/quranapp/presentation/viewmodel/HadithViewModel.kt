package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.data.repository.HadithRepository
import com.example.quranapp.domain.model.Hadith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HadithViewModel @Inject constructor(
    private val repository: HadithRepository
) : ViewModel() {

    private val _currentHadith = MutableStateFlow<Hadith?>(null)
    val currentHadith: StateFlow<Hadith?> = _currentHadith.asStateFlow()

    init {
        fetchDailyHadith()
    }

    private fun fetchDailyHadith() {
        viewModelScope.launch {
            _currentHadith.value = repository.getRandomHadith()
        }
    }
}
