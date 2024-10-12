package com.fundamentalandroid.dicodingevents.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fundamentalandroid.dicodingevents.data.respons.EventResponse
import com.fundamentalandroid.dicodingevents.data.respons.ListEventsItem
import com.fundamentalandroid.dicodingevents.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishedViewModel : ViewModel() {

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>(emptyList())
    val finishedEvents: LiveData<List<ListEventsItem>> get() = _finishedEvents

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun loadFinishedEvents() {
        _loading.value = true

        ApiConfig.getApiService().getEvents(0).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _loading.value = false
                if (response.isSuccessful) {
                    val finishedEvents = response.body()?.listEvents ?: emptyList()
                    _finishedEvents.value = finishedEvents
                } else {
                    _errorMessage.value = "Gagal memuat acara selesai: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _loading.value = false
                _errorMessage.value = "Kesalahan jaringan: ${t.message ?: "Gagal memuat."}"
            }
        })
    }
}
