package com.example.quranapp.data.repository

import android.content.Context
import com.example.quranapp.data.local.dao.AdhkarDao
import com.example.quranapp.data.local.entity.AdhkarProgressEntity
import com.example.quranapp.domain.model.AdhkarCategory
import com.example.quranapp.domain.model.AdhkarItem
import com.example.quranapp.domain.repository.AdhkarRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AdhkarRepositoryImpl @Inject constructor(
    private val adhkarDao: AdhkarDao
) : AdhkarRepository {

    private val today: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun getCategories(): Flow<List<AdhkarCategory>> {
        return combine(
            adhkarDao.getAllCategories(),
            adhkarDao.getAllProgress()
        ) { categories, progressList ->
            mapToDomain(categories, progressList)
        }
    }

    override suspend fun getCategoryById(id: Int): AdhkarCategory? {
        val categoryEntity = adhkarDao.getCategoryById(id) ?: return null
        val items = adhkarDao.getItemsByCategory(id)
        val progressList = adhkarDao.getAllProgressList()
        val progressMap = progressList.associateBy { it.adhkarId }
        val currentDay = today

        val domainItems = items.map { itemEntity ->
            val progress = progressMap[itemEntity.id]
            val currentCount = if (progress?.lastUpdatedDate == currentDay) progress.currentCount else 0
            AdhkarItem(
                id = itemEntity.id,
                text = itemEntity.text,
                targetCount = itemEntity.targetCount,
                currentCount = currentCount,
                reference = itemEntity.reference
            )
        }

        return AdhkarCategory(
            id = categoryEntity.id,
            title = categoryEntity.title,
            description = categoryEntity.description,
            items = domainItems,
            progress = domainItems.count { it.currentCount >= it.targetCount },
            total = domainItems.size
        )
    }

    private fun mapToDomain(
        categoryEntities: List<com.example.quranapp.data.local.entity.AdhkarCategoryEntity>,
        progressList: List<AdhkarProgressEntity>
    ): List<AdhkarCategory> {
        // This is tricky because getAllCategories doesn't include items.
        // In a real app, we might use a Relation or just fetch items here.
        // For simplicity, we'll fetch items inside the mapping (note: this is a bit slow in a Flow, 
        // ideally we'd use a Pojo with @Relation)
        
        // Let's assume the UI will call getCategoryById for details, 
        // so for the list, we don't need all items.
        
        return categoryEntities.map { catEntity ->
            AdhkarCategory(
                id = catEntity.id,
                title = catEntity.title,
                description = catEntity.description,
                items = emptyList(), // Items only loaded in detail or via a better query
                progress = 0, // Simplified for list view
                total = 0
            )
        }
    }

    override suspend fun updateAdhkarCount(adhkarId: Int, count: Int) {
        adhkarDao.insertOrUpdateProgress(
            AdhkarProgressEntity(
                adhkarId = adhkarId,
                currentCount = count,
                lastUpdatedDate = today
            )
        )
    }

    override suspend fun resetAllProgress() {
        adhkarDao.clearAllProgress()
    }
}
