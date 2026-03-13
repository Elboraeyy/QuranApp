package com.example.quranapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.quranapp.data.local.entity.TasbihEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TasbihDao {
    @Query("SELECT * FROM tasbih ORDER BY isDefault DESC, id ASC")
    fun getAllTasbihs(): Flow<List<TasbihEntity>>

    @Query("SELECT * FROM tasbih WHERE id = :id")
    suspend fun getTasbihById(id: Int): TasbihEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasbih(tasbih: TasbihEntity): Long

    @Update
    suspend fun updateTasbih(tasbih: TasbihEntity)

    @Delete
    suspend fun deleteTasbih(tasbih: TasbihEntity)

    @Query("SELECT SUM(totalCompletions) FROM tasbih")
    fun getTotalLifetimeCompletions(): Flow<Int?>
    
    @Query("SELECT COUNT(*) FROM tasbih")
    suspend fun getTasbihCount(): Int
}
