package com.fundamentalandroid.dicodingevents.data.retrofit

import com.fundamentalandroid.dicodingevents.data.respons.EventResponse
import com.fundamentalandroid.dicodingevents.data.respons.ListEventsItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int
    ): Call<EventResponse>

    @GET("events")
    fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") query: String
    ): Call<List<ListEventsItem>>
}
