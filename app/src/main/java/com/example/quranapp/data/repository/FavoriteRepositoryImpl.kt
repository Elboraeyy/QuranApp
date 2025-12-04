package com.example.quranapp.data.repository

import com.example.quranapp.data.local.dao.FavoriteDao
import com.example.quranapp.data.local.mapper.toDomain
import com.example.quranapp.data.local.mapper.toEntity
import com.example.quranapp.domain.model.Favorite
import com.example.quranapp.domain.model.FavoriteType
import com.example.quranapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {
    
    override suspend fun addFavorite(favorite: Favorite): Long {
        return favoriteDao.insertFavorite(favorite.toEntity())
    }
    
    override suspend fun deleteFavorite(favorite: Favorite) {
        favoriteDao.deleteFavorite(favorite.toEntity())
    }
    
    override suspend fun deleteFavoriteById(id: Long) {
        favoriteDao.deleteFavoriteById(id)
    }
    
    override suspend fun getAllFavorites(): List<Favorite> {
        return favoriteDao.getAllFavoritesList().map { it.toDomain() }
    }
    
    override suspend fun getFavoritesByType(type: FavoriteType): List<Favorite> {
        return favoriteDao.getFavoritesByType(type.name).map { it.toDomain() }
    }
    
    override fun getAllFavoritesFlow(): Flow<List<Favorite>> {
        return favoriteDao.getAllFavorites().map { favorites -> favorites.map { it.toDomain() } }
    }
    
    override suspend fun isFavorite(
        type: FavoriteType,
        surahNumber: Int?,
        ayahNumber: Int?,
        reciterId: String?,
        tafsirId: String?
    ): Boolean {
        return favoriteDao.isFavorite(type.name, surahNumber, ayahNumber, reciterId, tafsirId)
    }
}

