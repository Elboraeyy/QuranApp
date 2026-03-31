package com.example.quranapp.domain.usecase

import com.example.quranapp.domain.model.Hadith
import com.example.quranapp.domain.repository.HadithRepository
import javax.inject.Inject

class SearchHadithUseCase @Inject constructor(
    private val repository: HadithRepository
) {
    suspend operator fun invoke(query: String): List<Hadith> {
        if (query.isBlank()) return emptyList()
        // The repository might need a search method in its interface
        return repository.searchHadiths(query)
    }
}
