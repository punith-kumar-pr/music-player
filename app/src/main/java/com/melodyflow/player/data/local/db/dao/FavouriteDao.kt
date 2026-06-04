package com.melodyflow.player.data.local.db.dao

import androidx.room.*
import com.melodyflow.player.data.local.db.entity.FavouriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    @Query("SELECT songId FROM favourites")
    fun getAllFavouriteIds(): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE songId = :songId)")
    fun isFavourite(songId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavourite(favourite: FavouriteEntity)

    @Query("DELETE FROM favourites WHERE songId = :songId")
    suspend fun removeFavourite(songId: Long)
}
