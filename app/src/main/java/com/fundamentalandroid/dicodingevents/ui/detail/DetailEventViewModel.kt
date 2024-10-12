package com.fundamentalandroid.dicodingevents.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fundamentalandroid.dicodingevents.data.respons.ListEventsItem

class DetailEventViewModel : ViewModel() {

    private val _eventItem = MutableLiveData<ListEventsItem>()
    val eventItem: LiveData<ListEventsItem> get() = _eventItem

    fun setEventItem(item: ListEventsItem) {
        _eventItem.value = item
    }
}
