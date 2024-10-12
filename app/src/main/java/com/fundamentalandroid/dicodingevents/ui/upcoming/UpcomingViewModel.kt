package com.fundamentalandroid.dicodingevents.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fundamentalandroid.dicodingevents.data.respons.EventResponse
import com.fundamentalandroid.dicodingevents.data.respons.ListEventsItem
import com.fundamentalandroid.dicodingevents.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingViewModel : ViewModel() {

    private val _events = MutableLiveData<List<ListEventsItem>>()
    val events: LiveData<List<ListEventsItem>> get() = _events

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun findEvents() {
        _loading.value = true
        val client = ApiConfig.getApiService().getEvents(1)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _loading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _events.value = responseBody.listEvents
                    } else {
                        _errorMessage.value = "Gagal memuat data: ${responseBody?.message ?: "Terjadi kesalahan."}"
                    }
                } else {
                    _errorMessage.value = "Kesalahan respons: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _loading.value = false
                _errorMessage.value = "Kesalahan jaringan: ${t.message ?: "Gagal memuat."}"
            }
        })
    }
}
