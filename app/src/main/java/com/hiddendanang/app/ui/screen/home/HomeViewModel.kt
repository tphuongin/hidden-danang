package com.hiddendanang.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.repository.AuthRepository
import com.hiddendanang.app.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Trạng thái UI cho Màn hình Home
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val popularPlaces: List<Place> = emptyList(),
    val userName: String? = null,
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    // Khởi tạo Repository bằng by lazy để tránh lỗi CONFIGURATION_NOT_FOUND
    private val locationRepository: LocationRepository by lazy { LocationRepository() }
    private val authRepository: AuthRepository by lazy { AuthRepository() }

    // StateFlow để giữ trạng thái UI
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Tải dữ liệu ngay khi ViewModel được tạo
        fetchPopularPlaces()
        loadUserData()
    }

    /**
     * Lấy danh sách địa điểm phổ biến từ Repository
     */
    fun fetchPopularPlaces() {
        viewModelScope.launch {
            // Cập nhật trạng thái sang Loading
            _uiState.update { it.copy(isLoading = true, error = null) }

            locationRepository.getPopularPlaces().fold(
                onSuccess = { places ->
                    // Thành công, cập nhật danh sách
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            popularPlaces = places
                        )
                    }
                },
                onFailure = { e ->
                    // Thất bại, cập nhật lỗi
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Không thể tải địa điểm"
                        )
                    }
                }
            )
        }
    }

    /**
     * Lấy thông tin người dùng hiện tại (nếu có)
     */
    fun loadUserData() {
        // Không cần viewModelScope vì getCurrentUser không phải suspend fun
        val user = authRepository.getCurrentUser()
        if (user != null) {
            _uiState.update {
                it.copy(userName = user.displayName)
            }
        }
    }

    /**
     * Xóa thông báo lỗi sau khi đã hiển thị
     */
    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }
}
