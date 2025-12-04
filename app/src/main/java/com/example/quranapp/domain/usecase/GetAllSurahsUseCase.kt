package com.example.quranapp.domain.usecase

import com.example.quranapp.domain.model.Surah
import com.example.quranapp.domain.repository.QuranRepository
import javax.inject.Inject

class GetAllSurahsUseCase @Inject constructor(
    private val repository: QuranRepository
) {
    suspend operator fun invoke(): List<Surah> {
        return repository.getAllSurahs()
    }
}

