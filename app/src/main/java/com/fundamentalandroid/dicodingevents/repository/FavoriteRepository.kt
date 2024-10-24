package com.fundamentalandroid.dicodingevents.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fundamentalandroid.dicodingevents.data.local.room.FavoriteDao
import com.fundamentalandroid.dicodingevents.data.local.entity.FavoriteEntity
import com.fundamentalandroid.dicodingevents.data.local.room.FavoriteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteRepository(application: Application) {
    private val mFavoriteDao: FavoriteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteRoomDatabase.getDatabase(application)
        mFavoriteDao = db.favoriteDao()
    }

    fun getAllFavorites(): LiveData<List<FavoriteEntity>> = mFavoriteDao.getAllFavorites()

    fun insert(favoriteEntity: FavoriteEntity) {
        executorService.execute { mFavoriteDao.insert(favoriteEntity) }
    }

    fun delete(favoriteEntity: FavoriteEntity) {
        executorService.execute { mFavoriteDao.delete(favoriteEntity) }
    }

    fun update(favoriteEntity: FavoriteEntity) {
        executorService.execute { mFavoriteDao.update(favoriteEntity) }
    }

    fun isFavorited(eventId: Int): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        executorService.execute {
            val favorite = mFavoriteDao.getFavoriteById(eventId)
            result.postValue(favorite != null)
        }
        return result
    }
}
