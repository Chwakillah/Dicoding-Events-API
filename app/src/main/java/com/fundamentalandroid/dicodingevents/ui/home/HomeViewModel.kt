package com.fundamentalandroid.dicodingevents.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fundamentalandroid.dicodingevents.data.respons.EventResponse
import com.fundamentalandroid.dicodingevents.data.respons.ListEventsItem
import com.fundamentalandroid.dicodingevents.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>(emptyList())
    val upcomingEvents: LiveData<List<ListEventsItem>> get() = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>(emptyList())
    val finishedEvents: LiveData<List<ListEventsItem>> get() = _finishedEvents

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun loadEvents(query: String = "") {
        _loading.value = true

        // Load upcoming events (active = 1)
        ApiConfig.getApiService().getEvents(1, query).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _loading.value = false
                if (response.isSuccessful) {
                    val upcomingEvents = response.body()?.listEvents?.take(5) ?: emptyList()
                    _upcomingEvents.value = upcomingEvents
                } else {
                    _errorMessage.value = "Gagal memuat acara mendatang: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _loading.value = false
                _errorMessage.value = "Kesalahan jaringan: ${t.message ?: "Gagal memuat."}"
            }
        })

        // Load finished events (active = 0)
        ApiConfig.getApiService().getEvents(0, query).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val finishedEvents = response.body()?.listEvents?.take(5) ?: emptyList()
                    _finishedEvents.value = finishedEvents
                } else {
                    _errorMessage.value = "Gagal memuat acara selesai: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _errorMessage.value = "Kesalahan jaringan: ${t.message ?: "Gagal memuat."}"
            }
        })
    }

    // Add search function
    fun searchEvents(query: String) {
        if (query.isEmpty()) {
            loadEvents() // Load all events if query is empty
        } else {
            loadEvents(query) // Load filtered events
        }
    }
}