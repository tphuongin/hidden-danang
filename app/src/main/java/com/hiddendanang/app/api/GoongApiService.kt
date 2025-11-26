package com.hiddendanang.app.api

import com.hiddendanang.app.data.model.goongmodel.DirectionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoongApiService {

    // Directions API
    @GET("v2/direction")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("vehicle") vehicle: String = "car",
        @Query("api_key") apiKey: String
    ): DirectionResponse
    
}