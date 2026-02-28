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
    @ApplicationContext private val context: Context,
    private val adhkarDao: AdhkarDao
) : AdhkarRepository {

    private val gson = Gson()
    private val today: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private data class AdhkarJsonRoot(val categories: List<AdhkarCategoryJson>)
    private data class AdhkarCategoryJson(val id: Int, val title: String, val description: String, val items: List<AdhkarItemJson>)
    private data class AdhkarItemJson(val id: Int, val text: String, val count: Int, val reference: String)

    private val staticCategories: List<AdhkarCategoryJson> by lazy {
        try {
            val jsonString = context.assets.open("adhkar.json").bufferedReader().use { it.readText() }
            // The JSON is now an object with a "categories" field
            gson.fromJson(jsonString, AdhkarJsonRoot::class.java).categories
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getCategories(): Flow<List<AdhkarCategory>> {
        return adhkarDao.getAllProgress().map { progressList ->
            mapToDomain(progressList)
        }
    }

    override suspend fun getCategoryById(id: Int): AdhkarCategory? {
        // We can't easily get a Flow for just one category without re-mapping everything,
        // but for the detail screen, we can get the latest progress and map it.
        val progressList = adhkarDao.getAllProgressList() // Need to add this to DAO or use Flow
        return mapToDomain(progressList).find { it.id == id }
    }

    private fun mapToDomain(progressList: List<AdhkarProgressEntity>): List<AdhkarCategory> {
        val progressMap = progressList.associateBy { it.adhkarId }
        val currentDay = today
        return staticCategories.map { catJson ->
            val items = catJson.items.map { itemJson ->
                val progress = progressMap[itemJson.id]
                val currentCount = if (progress?.lastUpdatedDate == currentDay) progress.currentCount else 0
                AdhkarItem(
                    id = itemJson.id,
                    text = itemJson.text,
                    targetCount = itemJson.count,
                    currentCount = currentCount,
                    reference = itemJson.reference
                )
            }
            val completedCount = items.count { it.currentCount >= it.targetCount }
            AdhkarCategory(
                id = catJson.id,
                title = catJson.title,
                description = catJson.description,
                items = items,
                progress = completedCount,
                total = items.size
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
