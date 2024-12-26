package com.example.appevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.appevent.data.local.entity.FavoriteEvent

@Dao
interface FavoriteEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(event: FavoriteEvent)

    @Delete
    suspend fun deleteFavorite(event: FavoriteEvent)

    @Query("SELECT * FROM favorite_events")
    fun getAllFavorites(): LiveData<List<FavoriteEvent>>

    @Query("SELECT * FROM favorite_events WHERE id = :eventId LIMIT 1")
    suspend fun getFavoriteById(eventId: Int): FavoriteEvent?
}
