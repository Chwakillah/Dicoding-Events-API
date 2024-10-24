package com.fundamentalandroid.dicodingevents.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fundamentalandroid.dicodingevents.data.local.entity.FavoriteEntity

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favoriteEntity: FavoriteEntity)

    @Update
    fun update(favoriteEntity: FavoriteEntity)

    @Delete
    fun delete(favoriteEntity: FavoriteEntity)

    @Query("SELECT * from favoriteentity ORDER BY id ASC")
    fun getAllFavorites(): LiveData<List<FavoriteEntity>>

    @Query("SELECT * FROM favoriteentity WHERE id = :eventId LIMIT 1")
    fun getFavoriteById(eventId: Int): FavoriteEntity?
}