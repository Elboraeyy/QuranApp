package com.example.quranapp.data.repository

import android.content.Context
import com.example.quranapp.data.local.dao.TasbihDao
import com.example.quranapp.data.local.entity.TasbihEntity
import com.example.quranapp.domain.model.TasbihItem
import com.example.quranapp.domain.repository.TasbihRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TasbihRepositoryImpl(
    private val context: Context,
    private val tasbihDao: TasbihDao
) : TasbihRepository {

    override fun getAllTasbihs(): Flow<List<TasbihItem>> {
        return tasbihDao.getAllTasbihs().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun getTasbihById(id: Int): TasbihItem? {
        return tasbihDao.getTasbihById(id)?.toDomainModel()
    }

    override suspend fun addTasbih(phrase: String, targetCount: Int) {
        val entity = TasbihEntity(phrase = phrase, targetCount = targetCount, isDefault = false)
        tasbihDao.insertTasbih(entity)
    }

    override suspend fun updateTasbih(item: TasbihItem) {
        tasbihDao.updateTasbih(item.toEntity())
    }

    override suspend fun deleteTasbih(item: TasbihItem) {
        tasbihDao.deleteTasbih(item.toEntity())
    }

    override suspend fun incrementTasbihCompletions(id: Int) {
        val entity = tasbihDao.getTasbihById(id)
        if (entity != null) {
            tasbihDao.updateTasbih(entity.copy(totalCompletions = entity.totalCompletions + 1))
        }
    }

    override fun getTotalLifetimeCompletions(): Flow<Int> {
        return tasbihDao.getTotalLifetimeCompletions().map { it ?: 0 }
    }

    override suspend fun initializeDefaultTasbihsIfNeeded() {
        val count = tasbihDao.getTasbihCount()
        if (count == 0) {
            val defaults = listOf(
                TasbihEntity(phrase = "سُبْحَانَ اللَّهِ", targetCount = 33, isDefault = true),
                TasbihEntity(phrase = "الْحَمْدُ لِلَّهِ", targetCount = 33, isDefault = true),
                TasbihEntity(phrase = "اللَّهُ أَكْبَرُ", targetCount = 33, isDefault = true),
                TasbihEntity(phrase = "لَا إِلَهَ إِلَّا اللَّهُ", targetCount = 100, isDefault = true)
            )
            defaults.forEach { tasbihDao.insertTasbih(it) }
        }
    }

    private fun TasbihEntity.toDomainModel(): TasbihItem {
        return TasbihItem(
            id = id,
            phrase = phrase,
            targetCount = targetCount,
            totalCompletions = totalCompletions,
            isDefault = isDefault
        )
    }

    private fun TasbihItem.toEntity(): TasbihEntity {
        return TasbihEntity(
            id = id,
            phrase = phrase,
            targetCount = targetCount,
            totalCompletions = totalCompletions,
            isDefault = isDefault
        )
    }
}
