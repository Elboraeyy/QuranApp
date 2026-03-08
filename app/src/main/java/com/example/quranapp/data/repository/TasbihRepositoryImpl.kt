package com.example.quranapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.quranapp.domain.model.ZikrPhrase
import com.example.quranapp.domain.repository.TasbihRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tasbih_prefs")

class TasbihRepositoryImpl(private val context: Context) : TasbihRepository {

    companion object {
        private val TOTAL_COUNT_KEY = intPreferencesKey("total_tasbih_count")
        private val SELECTED_PHRASE_ID_KEY = intPreferencesKey("selected_phrase_id")
    }

    override fun getTotalCountFlow(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[TOTAL_COUNT_KEY] ?: 0
        }
    }

    override suspend fun incrementTotalCount(amount: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[TOTAL_COUNT_KEY] ?: 0
            preferences[TOTAL_COUNT_KEY] = current + amount
        }
    }

    override suspend fun getStandardPhrases(): List<ZikrPhrase> {
        return listOf(
            ZikrPhrase(1, "سُبْحَانَ اللَّهِ", "Glory be to Allah", 33),
            ZikrPhrase(2, "الْحَمْدُ لِلَّهِ", "Praise be to Allah", 33),
            ZikrPhrase(3, "اللَّهُ أَكْبَرُ", "Allah is the Greatest", 33),
            ZikrPhrase(4, "لَا إِلَهَ إِلَّا اللَّهُ", "There is no god but Allah", 100),
            ZikrPhrase(5, "أَسْتَغْفِرُ اللَّهَ", "I seek forgiveness from Allah", 100)
        )
    }

    override fun getSelectedPhraseIdFlow(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[SELECTED_PHRASE_ID_KEY] ?: 1
        }
    }

    override suspend fun saveSelectedPhraseId(id: Int) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_PHRASE_ID_KEY] = id
        }
    }
}
