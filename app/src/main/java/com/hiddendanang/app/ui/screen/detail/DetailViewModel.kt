package com.hiddendanang.app.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hiddendanang.app.data.model.Favorite
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.repository.FavoritesRepository
import com.hiddendanang.app.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val place: Place? = null,
    val isFavorite: Boolean = false,
    val nearbyPlaces: List<Place> = emptyList(),
    val error: String? = null,
    val favoriteIds: Set<String> = emptySet(),
)

class DetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val locationRepository: LocationRepository by lazy { LocationRepository() }
    private val favoritesRepository: FavoritesRepository by lazy { FavoritesRepository() }
    val favoriteIds: StateFlow<Set<String>> =
        favoritesRepository.getFavoriteIdsStream()
            .catch { emit(emptySet<String>()) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptySet()
            )

    // Lấy "id" từ arguments của navigation
    private val placeId: String = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        // Lắng nghe 3 luồng dữ liệu cùng lúc
        listenToDataChanges()
    }

    private fun listenToDataChanges() {
        viewModelScope.launch {
            // Luồng 2: Lấy chi tiết địa điểm
            val placeResult = locationRepository.getPlaceDetail(placeId)

            // Xử lý lỗi của Luồng 2 (quan trọng nhất)
            if (placeResult.isFailure) {
                _uiState.update { it.copy(isLoading = false, error = placeResult.exceptionOrNull()?.message) }
                return@launch
            }
            val place = placeResult.getOrNull()

            // SỬA LỖI 2: Luồng 3: Lấy các địa điểm lân cận
            // (Chỉ chạy sau khi đã có `place` để lấy geohash)
            val nearbyPlacesResult = if (place != null && place.coordinates.geohash.isNotEmpty()) {
                locationRepository.getNearbyPlaces(place.coordinates.geohash)
            } else {
                Result.success(emptyList()) // Trả về rỗng nếu không có geohash
            }

            if (nearbyPlacesResult.isFailure) {
                // Không nghiêm trọng bằng lỗi 1, vẫn có thể hiển thị trang
                _uiState.update { it.copy(error = nearbyPlacesResult.exceptionOrNull()?.message) }
            }

            val nearbyPlaces = nearbyPlacesResult.getOrNull() ?: emptyList()
            // Gộp Luồng 1 (favoriteIds) với data đã fetch (place)
            favoriteIds
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { favoriteIds ->
                    // Cập nhật UI
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            place = place,
                            // Trạng thái yêu thích = ID của place này có trong danh sách IDs
                            isFavorite = (place?.id in favoriteIds),
                            // SỬA LỖI: Truyền cả favoriteIds vào UiState
                            favoriteIds = favoriteIds,
                            nearbyPlaces = nearbyPlaces.filter { p -> p.id != placeId }
                        )
                    }
                }
        }
    }


    private fun addFavorite(place: Place) {
        viewModelScope.launch {
            // Chuyển đổi 'Place' sang 'Favorite'
            val favoriteData = Favorite(
                location_id = place.id,
                location_name = place.name,
                location_category = place.subcategory,
                location_image = place.images.firstOrNull()?.url ?: "",
                location_rate = place.rating_summary.average
                // user_id sẽ được thêm bởi Repository
            )
            val result = favoritesRepository.addFavorite(favoriteData)
            result.onFailure { e ->
                _uiState.update { it.copy(error = "Lỗi khi thêm: ${e.message}") }
            }
        }
    }

    fun toggleFavorite(place: Place) {
        val currentIds = favoriteIds.value

        if (place.id in currentIds) {
            // Nếu đã có -> Xóa
            removeFromFavorites(place.id) // Dùng lại hàm cũ
        } else {
            // Nếu chưa có -> Thêm
            addFavorite(place)
        }
    }
    fun removeFromFavorites(placeId: String) {
        viewModelScope.launch {
            val result = favoritesRepository.removeFavoriteByPlaceId(placeId)
            result.onFailure { e ->
                _uiState.update { it.copy(error = "Lỗi khi xóa: ${e.message}") }
            }
            // Thành công: không cần làm gì, Flow sẽ tự động cập nhật list
        }
    }

    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }
}

