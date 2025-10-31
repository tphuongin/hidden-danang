// File: data/repository/GoongRepository.kt
package com.hiddendanang.app.data.repository

import android.util.Log
import com.hiddendanang.app.api.GoongApiService
import com.hiddendanang.app.data.model.goongmodel.DirectionsResponse
import okhttp3.ResponseBody

class GoongRepository(private val apiService: GoongApiService) {

    suspend fun getDirections(
        origin: String,
        destination: String,
        apiKey: String
    ): DirectionsResponse {
        return apiService.getDirections(origin, destination, "car", apiKey)
    }

    suspend fun getStaticMap(
        origin: String,
        destination: String,
        vehicle: String = "car",
        width: Int,
        height: Int,
        type: String = "fastest",
        color: String = "#253494",
        apiKey: String
    ): ResponseBody {
        return apiService.getStaticMap(
            origin = origin,
            destination = destination,
            vehicle = vehicle,
            width = width,
            height = height,
            type = type,
            color = color,
            apiKey = apiKey
        )
    }
}