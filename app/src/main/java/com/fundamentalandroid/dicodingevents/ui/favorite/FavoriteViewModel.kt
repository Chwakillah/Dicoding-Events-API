package com.fundamentalandroid.dicodingevents.ui.favorite

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.fundamentalandroid.dicodingevents.db.FavoriteEntity
import com.fundamentalandroid.dicodingevents.repository.FavoriteRepository

class FavoriteViewModel(application: Application) : ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)
    fun getAllNotes(): LiveData<List<FavoriteEntity>> = mFavoriteRepository.getAllNotes()
}