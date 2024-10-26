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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private suspend fun checkIfFavorited(eventId: Int) {
        try {
            _isLoading.value = true
            val isFavored = mFavoriteRepository.isFavorited(eventId)
            _isFavorited.postValue(isFavored)
            _error.postValue(null)
        } catch (e: Exception) {
            _error.postValue(e.message ?: "Unknown error occurred")
        } finally {
            _isLoading.value = false
        }
    }

    fun setEventItem(item: ListEventsItem) {
        _eventItem.value = item
        viewModelScope.launch {
            checkIfFavorited(item.id)
        }
    }

    fun insert(favoriteEntity: FavoriteEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                mFavoriteRepository.insert(favoriteEntity)
                _isFavorited.postValue(true)
                _error.postValue(null)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Failed to add to favorites")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun delete(favoriteEntity: FavoriteEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                mFavoriteRepository.delete(favoriteEntity)
                _isFavorited.postValue(false)
                _error.postValue(null)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Failed to remove from favorites")
            } finally {
                _isLoading.value = false
            }
        }
    }

}