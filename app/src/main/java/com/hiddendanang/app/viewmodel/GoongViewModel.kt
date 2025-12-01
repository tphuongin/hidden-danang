// File: viewmodel/GoongViewModel.kt
package com.hiddendanang.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hiddendanang.app.R
import com.hiddendanang.app.api.GoongRetrofitClient
import com.hiddendanang.app.data.model.goongmodel.DirectionResponse
import com.hiddendanang.app.data.repository.GoongRepository
import com.hiddendanang.app.utils.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.repository.LocationRepository

class GoongViewModel(application: Application) : AndroidViewModel(application) {
    private val apiKey = application.getString(R.string.goong_api_key)
    private val apiService = GoongRetrofitClient.getInstance(application)
    private val repository = GoongRepository(apiService)
    private val locationService = LocationService(application)
    private val locationRepository = LocationRepository()
    
    private val _directionsResponse = MutableStateFlow<DirectionResponse?>(null)
    val directionsResponse: StateFlow<DirectionResponse?> = _directionsResponse.asStateFlow()
    private val _currentLocation = MutableStateFlow<String?>(null)
    val currentLocation: StateFlow<String?> = _currentLocation.asStateFlow()
    private val _nearbyPlaces = MutableStateFlow<List<Place>>(emptyList())
    val nearbyPlaces: StateFlow<List<Place>> = _nearbyPlaces.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun fetchDirections(origin: String, destination: String) {
        viewModelScope.launch {
            try {
                val response = repository.getDirections(origin, destination, apiKey)
                _directionsResponse.value = response
                Log.d("GoongViewModel", "Directions fetched successfully: $response")
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Lỗi khi lấy chỉ đường: ${e.message}"
                Log.e("GoongViewModel", "Error fetching directions: ${e.message}")
            }
        }
    }

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            try {
                // Kiểm tra quyền trước khi gọi
                if (hasLocationPermission()) {
                    val location = locationService.getCurrentLocation()
                    if (location != null) {
                        _currentLocation.value = location
                        Log.d("GoongViewModel", "Fetched current location: ${_currentLocation.value}")
                    } else {
                        _errorMessage.value = "Không thể lấy vị trí hiện tại"
                        Log.w("GoongViewModel", "Location is null")
                    }
                } else {
                    _errorMessage.value = "Không có quyền truy cập vị trí"
                    Log.w("GoongViewModel", "Location permission not granted")
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                _errorMessage.value = "Lỗi bảo mật khi lấy vị trí: ${e.message}"
                Log.e("GoongViewModel", "SecurityException: ${e.message}")
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Lỗi lấy vị trí: ${e.message}"
                Log.e("GoongViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun hasLocationPermission(): Boolean {
        return locationService.hasLocationPermission()
    }

    fun fetchNearbyPlaces(latitude: Double, longitude: Double) {
        android.util.Log.d("🗺️ MAP_NEARBY", "fetchNearbyPlaces called with lat: $latitude, lng: $longitude")
        
        viewModelScope.launch {
            try {
                // Generate geohash from coordinates
                val geohash = generateGeohash(latitude, longitude)
                android.util.Log.d("🗺️ MAP_NEARBY", "Fetching nearby places for geohash: $geohash (lat: $latitude, lng: $longitude)")
                
                val result = locationRepository.getNearbyPlaces(geohash)
                android.util.Log.d("🗺️ MAP_NEARBY", "getNearbyPlaces result: isSuccess=${result.isSuccess}, exception=${result.exceptionOrNull()?.message}")
                
                if (result.isSuccess) {
                    val places = result.getOrNull() ?: emptyList()
                    _nearbyPlaces.value = places
                    Log.d("🗺️ MAP_NEARBY", "✅ Fetched ${places.size} nearby places: ${places.map { it.name }}")
                } else {
                    _errorMessage.value = "Không thể lấy địa điểm gần đó: ${result.exceptionOrNull()?.message}"
                    Log.e("🗺️ MAP_NEARBY", "❌ Error fetching nearby places: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Lỗi khi lấy địa điểm gần: ${e.message}"
                Log.e("🗺️ MAP_NEARBY", "❌ Exception: ${e.message}")
            }
        }
    }

    private fun generateGeohash(latitude: Double, longitude: Double): String {
        // Proper geohash algorithm to match Firestore format (e.g., "w7gx6y")
        android.util.Log.d("🗺️ MAP_NEARBY", "generateGeohash input - lat: $latitude, lng: $longitude")
        
        try {
            val geohash = encodeGeohash(latitude, longitude, 6)
            android.util.Log.d("🗺️ MAP_NEARBY", "✅ Generated geohash: $geohash (6 chars)")
            return geohash
        } catch (e: Exception) {
            android.util.Log.e("🗺️ MAP_NEARBY", "❌ Error in generateGeohash: ${e.message}")
            return ""
        }
    }
    
    private fun encodeGeohash(latitude: Double, longitude: Double, precision: Int): String {
        val base32 = "0123456789bcdefghjkmnpqrstuvwxyz"
        var lat = doubleArrayOf(-90.0, 90.0)
        var lon = doubleArrayOf(-180.0, 180.0)
        
        val geohash = StringBuilder()
        var isEven = true
        var bit = 0
        var ch = 0
        
        while (geohash.length < precision) {
            val range = if (isEven) lon else lat
            val mid = (range[0] + range[1]) / 2.0
            
            if ((if (isEven) longitude else latitude) > mid) {
                ch = ch or (1 shl (4 - bit))
                range[0] = mid
            } else {
                range[1] = mid
            }
            
            isEven = !isEven
            bit++
            
            if (bit == 5) {
                geohash.append(base32[ch])
                bit = 0
                ch = 0
            }
        }
        
        return geohash.toString()
    }
}