package com.example.quranapp.domain.usecase

import com.example.quranapp.domain.model.Ayah
import com.example.quranapp.domain.repository.QuranRepository
import javax.inject.Inject

class GetJuzBoundariesUseCase @Inject constructor(
    private val repository: QuranRepository
) {
    suspend operator fun invoke(): List<Ayah> {
        return repository.getJuzBoundaries()
    }
}
