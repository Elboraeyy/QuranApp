package com.example.quranapp.domain.repository

import com.example.quranapp.domain.model.Favorite
import com.example.quranapp.domain.model.FavoriteType
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun addFavorite(favorite: Favorite): Long
    suspend fun deleteFavorite(favorite: Favorite)
    suspend fun deleteFavoriteById(id: Long)
    suspend fun getAllFavorites(): List<Favorite>
    suspend fun getFavoritesByType(type: FavoriteType): List<Favorite>
    fun getAllFavoritesFlow(): Flow<List<Favorite>>
    suspend fun isFavorite(type: FavoriteType, surahNumber: Int?, ayahNumber: Int?, reciterId: String?, tafsirId: String?): Boolean
    fun observeIsAdhkarFavorite(adhkarId: Int): Flow<Boolean>
    suspend fun getFavoriteByAdhkarId(adhkarId: Int): Favorite?
}

