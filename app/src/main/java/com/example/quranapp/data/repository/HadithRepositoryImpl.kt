package com.example.quranapp.data.repository

import com.example.quranapp.data.local.dao.HadithBookmarkDao
import com.example.quranapp.data.local.dao.HadithDao
import com.example.quranapp.data.local.entity.HadithBookmarkEntity
import com.example.quranapp.data.local.entity.HadithEntity
import com.example.quranapp.domain.model.Hadith
import com.example.quranapp.domain.model.HadithBook
import com.example.quranapp.domain.model.HadithCategory
import com.example.quranapp.domain.model.HadithVocab
import com.example.quranapp.domain.repository.HadithRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HadithRepositoryImpl @Inject constructor(
    private val hadithDao: HadithDao,
    private val bookmarkDao: HadithBookmarkDao
) : HadithRepository {

    override suspend fun getAllBooks(): List<HadithBook> {
        return hadithDao.getAllBooks().map {
            HadithBook(it.id, it.title, it.author, it.description)
        }
    }

    override suspend fun getAllCategories(): List<HadithCategory> {
        return hadithDao.getAllCategories().map {
            HadithCategory(it.id, it.title)
        }
    }

    override suspend fun getHadithsByBook(bookId: Int): List<Hadith> {
        return hadithDao.getHadithsByBook(bookId).map { it.toDomain() }
    }

    override suspend fun getHadithsByCategory(categoryId: Int): List<Hadith> {
        return hadithDao.getHadithsByCategory(categoryId).map { it.toDomain() }
    }

    override suspend fun getHadithById(id: Int): Hadith? {
        return hadithDao.getHadithById(id)?.toDomain()
    }

    override suspend fun getRandomHadith(): Flow<Hadith?> {
        // Since Room query RANDOM() is suspend, we'll wrap it in a flow if needed, 
        // or just return the value. The interface expects Flow.
        return kotlinx.coroutines.flow.flow {
            emit(hadithDao.getRandomHadith()?.toDomain())
        }
    }

    override suspend fun searchHadiths(query: String): List<Hadith> {
        return hadithDao.searchHadiths(query).map { it.toDomain() }
    }

    override fun getAllBookmarksFlow(): Flow<List<HadithBookmarkEntity>> {
        return bookmarkDao.getAllHadithBookmarks()
    }

    override fun isHadithBookmarked(hadithId: Int): Flow<Boolean> {
        return bookmarkDao.isBookmarked(hadithId)
    }

    override suspend fun toggleBookmark(hadith: Hadith) {
        val existing = bookmarkDao.getBookmarkById(hadith.id)
        if (existing != null) {
            bookmarkDao.deleteBookmark(existing)
        } else {
            bookmarkDao.insertBookmark(
                HadithBookmarkEntity(
                    hadithId = hadith.id,
                    bookId = hadith.bookId,
                    categoryId = hadith.categoryId
                )
            )
        }
    }

    private fun HadithEntity.toDomain(): Hadith {
        return Hadith(
            id = this.id,
            bookId = this.bookId,
            categoryId = this.categoryId,
            narrator = this.narrator,
            text = this.text,
            source = this.source,
            explanation = this.explanation,
            vocabulary = this.vocabulary,
            lifeApplications = this.lifeApplications
        )
    }
}
