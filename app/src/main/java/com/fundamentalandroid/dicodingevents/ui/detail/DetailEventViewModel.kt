//package com.fundamentalandroid.dicodingevents.ui.detail
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.fundamentalandroid.dicodingevents.data.respons.ListEventsItem
//import com.fundamentalandroid.dicodingevents.data.retrofit.ApiConfig
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class DetailEventViewModel : ViewModel() {
//
//    private val _eventDetails = MutableLiveData<ListEventsItem>()
//    val eventDetails: LiveData<ListEventsItem> = _eventDetails
//
//    fun getEventDetails(eventId: String) {
//        val client = ApiConfig.getApiService().getEventDetails(eventId)
//        client.enqueue(object : Callback<ListEventsItem> {
//            override fun onResponse(call: Call<ListEventsItem>, response: Response<ListEventsItem>) {
//                if (response.isSuccessful) {
//                    _eventDetails.value = response.body()
//                }
//            }
//
//            override fun onFailure(call: Call<ListEventsItem>, t: Throwable) {
//                // Handle error
//            }
//        })
//    }
//}