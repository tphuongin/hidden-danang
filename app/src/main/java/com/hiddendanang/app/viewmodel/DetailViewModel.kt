package com.hiddendanang.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.hiddendanang.app.data.model.Favorite
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.model.Review
import com.hiddendanang.app.data.repository.FavoritesRepository
import com.hiddendanang.app.data.repository.LocationRepository
import com.hiddendanang.app.data.repository.AuthRepository
import com.hiddendanang.app.data.repository.ReviewRepository
import com.hiddendanang.app.utils.AppConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class DetailUiState(
    val isLoading: Boolean = true,
    val place: Place? = null,
    val isFavorite: Boolean = false,
    val nearbyPlaces: List<Place> = emptyList(),
    val error: String? = null,
    val favoriteIds: Set<String> = emptySet(),
    val isReviewFormVisible: Boolean = false, // <-- TRẠNG THÁI MỚI
    val userReview: Review? = null, // Đánh giá hiện tại của user
    val allReviews: List<Review> = emptyList()
)

class DetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val placeIdArgument: String = savedStateHandle["id"] ?: ""

    private val reviewRepository: ReviewRepository by lazy { ReviewRepository() }
    private val authRepository: AuthRepository by lazy { AuthRepository() }
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
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    public fun listenToDataChanges(placeId : String, forceReload: Boolean = false) {
        viewModelScope.launch {
            if (!forceReload && placeId == _uiState.value.place?.id && !_uiState.value.isLoading) {
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }
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
            launch {
                reviewRepository.getAllReviewsStreamForPlace(placeId)
                    .catch { e ->
                        // Error fetching reviews
                    }
                    .collect { reviews ->
                        _uiState.update { it.copy(allReviews = reviews) }
                    }
            }

            // THÊM: Lắng nghe review của user hiện tại
            launch {
                reviewRepository.getUserReviewStream(placeId)
                    .catch { e ->
                        // Error fetching user review
                    }
                    .collect { userReview ->
                        _uiState.update { it.copy(userReview = userReview) }
                    }
            }
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


    // Hàm gọi khi UI muốn mở Form (bấm nút "Viết đánh giá")
    fun showReviewForm() {
        _uiState.update { it.copy(isReviewFormVisible = true) }
    }

    // Hàm gọi khi Form bị đóng (bấm Hủy hoặc Submit)
    fun hideReviewForm() {
        _uiState.update { it.copy(isReviewFormVisible = false) }
    }

    // Hàm gọi khi Form gửi dữ liệu
    // [VỊ TRÍ CẦN SỬA: Hàm submitUserReview]

    fun submitUserReview(rating: Int, comment: String) {
        // 1. Lấy thông tin cần thiết (để đảm bảo không bị null)
        val currentUser = authRepository.getCurrentUser()
        val currentUserId = currentUser?.uid ?: return
        val currentUserName = currentUser.displayName ?: AppConstants.DEFAULT_USER_NAME
        val placeId = _uiState.value.place?.id ?: return
        val currentUserPhotoUrl = currentUser.photoUrl?.toString() ?: ""


        val existingReview = _uiState.value.userReview // Lấy review cũ để giữ metadata
        // 2. Tạo đối tượng Review Model
        val reviewToSubmit = Review(
            rating = rating,
            comment = comment,
            user_id = currentUserId,
            user_name = currentUserName,
            user_photo_url = currentUserPhotoUrl,
            created_at = existingReview?.created_at, // Giữ lại ngày tạo nếu là chỉnh sửa
            updated_at = Timestamp(Date())
        )

        // 3. Gọi Repository để lưu DB
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = reviewRepository.submitOrUpdateReview(placeId, reviewToSubmit)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, error = null) }
                hideReviewForm() // Ẩn form sau khi gửi thành công
                // Refresh place data to update rating
                listenToDataChanges(placeId, forceReload = true)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }

    val allReviews: StateFlow<List<Review>> = _uiState
        .map { it.allReviews }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val isReviewsLoading: StateFlow<Boolean> = _uiState
        .map { it.isLoading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )


}

