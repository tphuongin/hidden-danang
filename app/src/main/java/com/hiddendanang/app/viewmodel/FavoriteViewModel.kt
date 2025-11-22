package com.hiddendanang.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hiddendanang.app.data.model.Favorite
import com.hiddendanang.app.data.repository.FavoritesRepository
import com.hiddendanang.app.data.model.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoriteUiState(
    val isLoading: Boolean = false,
    val favoritePlaces: List<Place> = emptyList(), // Hoặc Place
    val error: String? = null
)

class FavoritesViewModel : ViewModel() {

    private val favoritesRepository: FavoritesRepository by lazy { FavoritesRepository() }

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()
    val favoriteIds: StateFlow<Set<String>> =
        favoritesRepository.getFavoriteIdsStream()
            .catch {
                // Bắt lỗi (vd: chưa đăng nhập) và phát ra set rỗng
                emit(emptySet<String>())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptySet()
            )

    init {
         fetchFavorites()
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

    fun fetchFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Bắt đầu lắng nghe stream
            favoritesRepository.getFavoritePlacesStream() // Gọi hàm mới
                .catch { e ->
                    // Bắt lỗi nếu có (vd: chưa đăng nhập)
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { placesList ->
                    // Cập nhật UI mỗi khi có thay đổi
                    _uiState.update {
                        it.copy(isLoading = false, favoritePlaces = placesList)
                    }
                }
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
