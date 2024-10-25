package com.fundamentalandroid.dicodingevents.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.fundamentalandroid.dicodingevents.data.local.room.FavoriteDao
import com.fundamentalandroid.dicodingevents.data.local.entity.FavoriteEntity
import com.fundamentalandroid.dicodingevents.data.local.room.FavoriteRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteRepository(application: Application) {
    private val mFavoriteDao: FavoriteDao

    init {
        val db = FavoriteRoomDatabase.getDatabase(application)
        mFavoriteDao = db.favoriteDao()
    }

    fun getAllFavorites(): LiveData<List<FavoriteEntity>> = mFavoriteDao.getAllFavorites()

    suspend fun insert(favoriteEntity: FavoriteEntity) {
        withContext(Dispatchers.IO) {
            mFavoriteDao.insert(favoriteEntity)
        }
    }

    suspend fun delete(favoriteEntity: FavoriteEntity) {
        withContext(Dispatchers.IO) {
            mFavoriteDao.delete(favoriteEntity)
        }
    }

    suspend fun isFavorited(eventId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val favorite = mFavoriteDao.getFavoriteById(eventId)
            favorite != null
        }
    }

}

