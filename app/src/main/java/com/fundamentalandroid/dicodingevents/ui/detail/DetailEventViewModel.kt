package com.fundamentalandroid.dicodingevents.ui.detail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fundamentalandroid.dicodingevents.data.remote.respons.ListEventsItem
import com.fundamentalandroid.dicodingevents.data.local.entity.FavoriteEntity
import com.fundamentalandroid.dicodingevents.repository.FavoriteRepository

class DetailEventViewModel(application: Application) : ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    private val _eventItem = MutableLiveData<ListEventsItem>()
    val eventItem: LiveData<ListEventsItem> get() = _eventItem

    private val _isFavorited = MutableLiveData<Boolean>()
    val isFavorited: LiveData<Boolean> get() = _isFavorited

    private fun checkIfFavorited(eventId: Int) {
        mFavoriteRepository.isFavorited(eventId).observeForever { isFavored ->
            _isFavorited.value = isFavored
        }
    }

    fun setEventItem(item: ListEventsItem) {
        _eventItem.value = item
        checkIfFavorited(item.id)
    }

    fun insert(favoriteEntity: FavoriteEntity) {
        mFavoriteRepository.insert(favoriteEntity)
    }

    fun delete(favoriteEntity: FavoriteEntity) {
        mFavoriteRepository.delete(favoriteEntity)
    }
}

