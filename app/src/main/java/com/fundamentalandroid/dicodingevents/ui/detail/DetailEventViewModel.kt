package com.fundamentalandroid.dicodingevents.ui.detail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fundamentalandroid.dicodingevents.data.remote.respons.ListEventsItem
import com.fundamentalandroid.dicodingevents.data.local.entity.FavoriteEntity
import com.fundamentalandroid.dicodingevents.data.repository.FavoriteRepository
import kotlinx.coroutines.launch

class DetailEventViewModel(application: Application) : ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    private val _eventItem = MutableLiveData<ListEventsItem>()
    val eventItem: LiveData<ListEventsItem> get() = _eventItem

    private val _isFavorited = MutableLiveData<Boolean>()
    val isFavorited: LiveData<Boolean> get() = _isFavorited

    private suspend fun checkIfFavorited(eventId: Int) {
        val isFavored = mFavoriteRepository.isFavorited(eventId) // Now this returns a Boolean
        _isFavorited.postValue(isFavored)
    }

    fun setEventItem(item: ListEventsItem) {
        _eventItem.value = item
        viewModelScope.launch {
            checkIfFavorited(item.id)
        }
    }

    fun insert(favoriteEntity: FavoriteEntity) {
        viewModelScope.launch {
            mFavoriteRepository.insert(favoriteEntity)
        }
    }

    fun delete(favoriteEntity: FavoriteEntity) {
        viewModelScope.launch {
            mFavoriteRepository.delete(favoriteEntity)
        }
    }
}

