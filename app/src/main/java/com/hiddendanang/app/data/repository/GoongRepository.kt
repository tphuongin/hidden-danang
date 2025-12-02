// File: data/repository/GoongRepository.kt
package com.hiddendanang.app.data.repository

import com.hiddendanang.app.api.GoongApiService
import com.hiddendanang.app.data.model.goongmodel.DirectionResponse

class GoongRepository(private val apiService: GoongApiService) {

    suspend fun getDirections(
        origin: String,
        destination: String,
        apiKey: String
    ): DirectionResponse {
        return apiService.getDirections(origin, destination, "car", apiKey)
    }

}