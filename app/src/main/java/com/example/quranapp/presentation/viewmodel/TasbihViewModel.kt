package com.example.quranapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map

val Context.tasbihDataStore: DataStore<Preferences> by preferencesDataStore(name = "tasbih_prefs")

@HiltViewModel
class TasbihViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TASBIH_COUNT_KEY = intPreferencesKey("tasbih_count")

    private val _currentCount = MutableStateFlow(0)
    val currentCount: StateFlow<Int> = _currentCount.asStateFlow()

    init {
        viewModelScope.launch {
            context.tasbihDataStore.data.map { preferences ->
                preferences[TASBIH_COUNT_KEY] ?: 0
            }.collect { count ->
                _currentCount.value = count
            }
        }
    }

    fun incrementCount() {
        viewModelScope.launch {
            val newCount = _currentCount.value + 1
            _currentCount.value = newCount
            context.tasbihDataStore.edit { preferences ->
                preferences[TASBIH_COUNT_KEY] = newCount
            }
        }
    }

    fun resetCount() {
        viewModelScope.launch {
            _currentCount.value = 0
            context.tasbihDataStore.edit { preferences ->
                preferences[TASBIH_COUNT_KEY] = 0
            }
        }
    }
}
