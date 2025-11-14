// File: viewmodel/GoongViewModel.kt
package com.hiddendanang.app.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hiddendanang.app.R
import com.hiddendanang.app.api.GoongRetrofitClient
import com.hiddendanang.app.data.model.goongmodel.DirectionsResponse
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

    private val _directionsResponse = MutableStateFlow<DirectionsResponse?>(null)
    val directionsResponse: StateFlow<DirectionsResponse?> = _directionsResponse.asStateFlow()

    private val _staticMapBitmap = MutableStateFlow<Bitmap?>(null)
    val staticMapBitmap: StateFlow<Bitmap?> = _staticMapBitmap.asStateFlow()

    private val _currentLocation = MutableStateFlow<String?>(null)
    val currentLocation: StateFlow<String?> = _currentLocation.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun fetchDirections(origin: String, destination: String) {
        viewModelScope.launch {
            try {
                val response = repository.getDirections(origin, destination, apiKey)
                _directionsResponse.value = response
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Lỗi khi lấy chỉ đường: ${e.message}"
            }
        }
    }

    fun fetchStaticMap(
        origin: String,
        destination: String,
        width: Int = 500,
        height: Int = 400,
        vehicle: String = "car",
        type: String = "fastest",
        color: String = "#253494"
    ) {
        viewModelScope.launch {
            try {
                val response = repository.getStaticMap(
                    origin, destination, vehicle, width, height, type, color, apiKey
                )

                // Convert ResponseBody to Bitmap
                val inputStream = response.byteStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                _staticMapBitmap.value = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Lỗi khi tải bản đồ: ${e.message}"
            }
        }
    }

    // Trong GoongViewModel
    fun fetchCurrentLocation() {
        viewModelScope.launch {
            try {
                // Kiểm tra quyền trước khi gọi
                if (hasLocationPermission()) {
                    val location = locationService.getCurrentLocation()
                    _currentLocation.value = location
                } else {
                    _errorMessage.value = "Không có quyền truy cập vị trí"
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                _errorMessage.value = "Lỗi bảo mật khi lấy vị trí: ${e.message}"
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Lỗi lấy vị trí: ${e.message}"
            }
        }
    }

    fun hasLocationPermission(): Boolean {
        return locationService.hasLocationPermission()
    }
}