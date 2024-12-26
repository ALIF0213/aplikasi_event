package com.example.appevent.data.remote.retrofit

import com.example.appevent.data.remote.response.ResponseDetail
import com.example.appevent.data.remote.response.ResponseEvents
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int = 1,
        @Query("limit") limit: Int = 40
    ): Response<ResponseEvents>

    @GET("events")
    suspend fun searchEvents(
        @Query("active") active: Int = 0,
        @Query("q") query: String
    ): Response<ResponseEvents>

    @GET("events/{id}")
    suspend fun getEventDetail(
        @Path("id") id: Int
    ): Response<ResponseDetail>
}