package com.fundamentalandroid.dicodingevents.data.retrofit

import com.fundamentalandroid.dicodingevents.data.respons.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int
    ): Call<EventResponse>
}
