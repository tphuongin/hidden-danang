package com.hiddendanang.app.api

import com.hiddendanang.app.data.model.goongmodel.DirectionsResponse
import okhttp3.ResponseBody
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
    ): DirectionsResponse

    // Static Map API
    @GET("staticmap/route")
    suspend fun getStaticMap(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("vehicle") vehicle: String = "car",
        @Query("width") width: Int,
        @Query("height") height: Int,
        @Query("type") type: String = "fastest",
        @Query("color") color: String = "#253494",
        @Query("api_key") apiKey: String
    ): ResponseBody
}