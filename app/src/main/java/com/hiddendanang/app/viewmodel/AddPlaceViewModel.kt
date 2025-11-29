package com.hiddendanang.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.hiddendanang.app.data.model.*
import com.hiddendanang.app.data.repository.LocationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class AddPlaceUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    
    // Basic Info
    val name: String = "",
    val description: String = "",
    val categoryId: String = "",
    
    // Address & Location
    val address: String = "",
    val district: String = "",
    val latitude: Double = 0.0, 
    val longitude: Double = 0.0,
    
    // Images (Multiple)
    val selectedImages: List<Uri> = emptyList(),
    
    // Price Logic
    val hasPriceInfo: Boolean = true,
    val minPrice: String = "",
    val maxPrice: String = "",
    
    // Hours Logic
    val hasOpeningHours: Boolean = true,
    val isOpen247: Boolean = false,
    val openTime: String = "07:00",
    val closeTime: String = "22:00"
)

class AddPlaceViewModel : ViewModel() {
    private val locationRepository = LocationRepository()
    private val storageRef = Firebase.storage.reference

    private val _uiState = MutableStateFlow(AddPlaceUiState())
    val uiState: StateFlow<AddPlaceUiState> = _uiState.asStateFlow()

    // --- Input Handlers ---
    fun onNameChange(v: String) = _uiState.update { it.copy(name = v) }
    fun onAddressChange(v: String) = _uiState.update { it.copy(address = v) }
    fun onDistrictChange(v: String) = _uiState.update { it.copy(district = v) }
    fun onDescriptionChange(v: String) = _uiState.update { it.copy(description = v) }
    fun onCategoryChange(v: String) = _uiState.update { it.copy(categoryId = v) }

    // Location
    fun updateCoordinates(lat: Double, lng: Double) {
        _uiState.update { it.copy(latitude = lat, longitude = lng) }
    }

    // Images
    fun onImagesSelected(uris: List<Uri>) {
        _uiState.update { it.copy(selectedImages = uris) }
    }
    fun removeImage(uri: Uri) {
        _uiState.update { it.copy(selectedImages = it.selectedImages - uri) }
    }

    // Price
    fun togglePriceInfo(enabled: Boolean) = _uiState.update { it.copy(hasPriceInfo = enabled) }
    fun onMinPriceChange(v: String) = _uiState.update { it.copy(minPrice = v) }
    fun onMaxPriceChange(v: String) = _uiState.update { it.copy(maxPrice = v) }

    // Hours
    fun toggleOpeningHours(enabled: Boolean) = _uiState.update { it.copy(hasOpeningHours = enabled) }
    fun toggleOpen247(enabled: Boolean) = _uiState.update { it.copy(isOpen247 = enabled) }
    fun onOpenTimeChange(v: String) = _uiState.update { it.copy(openTime = v) }
    fun onCloseTimeChange(v: String) = _uiState.update { it.copy(closeTime = v) }

    // --- Submit Logic ---
    fun submitPlace() {
        val state = _uiState.value

        // 1. Validation cơ bản
        if (state.name.isBlank() || state.address.isBlank() || state.categoryId.isBlank()) {
            _uiState.update { it.copy(error = "Vui lòng điền tên, địa chỉ và danh mục.") }
            return
        }

        // 2. Validation Ảnh
        if (state.selectedImages.size < 3) {
            _uiState.update { it.copy(error = "Vui lòng chọn ít nhất 3 ảnh.") }
            return
        }

        // 3. Validation Giá
        var priceRange: PriceRange? = null
        if (state.hasPriceInfo) {
            val min = state.minPrice.toDoubleOrNull()
            val max = state.maxPrice.toDoubleOrNull()
            if (min == null || max == null) {
                _uiState.update { it.copy(error = "Giá tiền không hợp lệ.") }
                return
            }
            if (min > max) {
                _uiState.update { it.copy(error = "Giá thấp nhất không được lớn hơn giá cao nhất.") }
                return
            }
            priceRange = PriceRange(min, max, "VND")
        }

        // 4. Validation Giờ
        var openingHours: OpeningHours? = null
        if (state.hasOpeningHours) {
            if (state.isOpen247) {
                val allDay = DailySchedule("00:00", "23:59", false)
                openingHours = OpeningHours(mon = allDay, tue = allDay, wed = allDay, thu = allDay, fri = allDay, sat = allDay, sun = allDay)
            } else {
                val schedule = DailySchedule(state.openTime, state.closeTime, false)
                openingHours = OpeningHours(mon = schedule, tue = schedule, wed = schedule, thu = schedule, fri = schedule, sat = schedule, sun = schedule)
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // A. Upload ảnh
                val imageUploadDeferreds = state.selectedImages.map { uri ->
                    async {
                        val imageRef = storageRef.child("places/${UUID.randomUUID()}")
                        imageRef.putFile(uri).await()
                        imageRef.downloadUrl.await().toString()
                    }
                }
                val imageUrls = imageUploadDeferreds.awaitAll()
                val imageDetails = imageUrls.map { ImageDetail(url = it) }

                // B. Tạo Object Place
                val newPlace = Place(
                    name = state.name,
                    description = state.description,
                    category_id = state.categoryId,
                    address = Address(
                        formatted_address = state.address,
                        district = state.district
                    ),
                    coordinates = Coordinates(
                        latitude = state.latitude,
                        longitude = state.longitude,
                        geohash = "" 
                    ),
                    images = imageDetails,
                    // Sử dụng trường mới đã cập nhật trong Place.kt
                    price_range = priceRange, 
                    opening_hours = openingHours,
                    
                    status = "pending",
                    created_at = Timestamp.now()
                )

                // C. Lưu vào Firestore
                locationRepository.addPlace(newPlace)
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false, success = true) }
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Lỗi: ${e.message}") }
            }
        }
    }

    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun resetSuccess() {
        _uiState.update { it.copy(success = false) }
    }
}