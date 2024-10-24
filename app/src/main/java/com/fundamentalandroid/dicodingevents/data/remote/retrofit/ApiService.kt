package com.fundamentalandroid.dicodingevents.data.remote.retrofit

import com.fundamentalandroid.dicodingevents.data.remote.respons.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int,
        @Query("q") query: String? = null
    ): Call<EventResponse>
}
