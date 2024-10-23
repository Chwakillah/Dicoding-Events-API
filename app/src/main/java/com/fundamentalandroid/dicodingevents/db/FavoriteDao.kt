package com.fundamentalandroid.dicodingevents.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favoriteEntity: FavoriteEntity)

    @Update
    fun update(favoriteEntity: FavoriteEntity)

    @Delete
    fun delete(favoriteEntity: FavoriteEntity)

    @Query("SELECT * from favoriteentity ORDER BY id ASC")
    fun getAllNotes(): LiveData<List<FavoriteEntity>>
}