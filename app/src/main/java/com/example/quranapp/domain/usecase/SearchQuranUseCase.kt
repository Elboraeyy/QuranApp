package com.example.quranapp.domain.usecase

import com.example.quranapp.domain.model.Ayah
import com.example.quranapp.domain.model.Surah
import com.example.quranapp.domain.repository.QuranRepository
import javax.inject.Inject

class SearchQuranUseCase @Inject constructor(
    private val repository: QuranRepository
) {
    suspend fun searchSurahs(query: String): List<Surah> {
        return repository.searchSurahs(query)
    }
    
    suspend fun searchAyat(query: String): List<Ayah> {
        return repository.searchAyat(query)
    }
}

