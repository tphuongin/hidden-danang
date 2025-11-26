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

class GoongViewModel(application: Application) : AndroidViewModel(application) {
    private val apiKey = application.getString(R.string.goong_api_key)
    private val apiService = GoongRetrofitClient.getInstance(application)
    private val repository = GoongRepository(apiService)
    private val locationService = LocationService(application)
    private val _directionsResponse = MutableStateFlow<DirectionResponse?>(null)
    val directionsResponse: StateFlow<DirectionResponse?> = _directionsResponse.asStateFlow()
    private val _currentLocation = MutableStateFlow<String?>(null)
    val currentLocation: StateFlow<String?> = _currentLocation.asStateFlow()

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
}