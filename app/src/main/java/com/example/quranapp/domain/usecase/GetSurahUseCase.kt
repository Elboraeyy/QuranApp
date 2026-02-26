package com.example.quranapp.domain.usecase

import com.example.quranapp.domain.model.Surah
import com.example.quranapp.domain.repository.QuranRepository
import javax.inject.Inject

class GetSurahUseCase @Inject constructor(
    private val repository: QuranRepository
) {
    suspend operator fun invoke(surahNumber: Int): Surah? {
        return repository.getSurahByNumber(surahNumber)
    }
}
