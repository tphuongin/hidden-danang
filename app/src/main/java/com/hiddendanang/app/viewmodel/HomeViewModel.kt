package com.hiddendanang.app.viewmodel

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
import kotlinx.coroutines.flow.flatMapLatest // Cần để chuyển đổi Flow khi category thay đổi
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch

/**
 * Trạng thái UI cho Màn hình Home
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val places: List<Place> = emptyList(), // ĐÃ SỬA: Dùng tên 'places' cho danh sách đã lọc
    val userName: String? = null,
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val locationRepository: LocationRepository by lazy { LocationRepository() }
    private val authRepository: AuthRepository by lazy { AuthRepository() }

    // 1. TRẠNG THÁI LỌC: Chứa ID danh mục hiện tại (Mặc định là "all")
    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // StateFlow để giữ trạng thái UI
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // 2. FLOW ĐỊA ĐIỂM ĐƯỢC LỌC (Thay thế cho hàm fetchPopularPlaces cũ)
    val placesStream: StateFlow<List<Place>> = _selectedCategory
        .flatMapLatest { categoryId ->
            // Khi categoryId thay đổi, gọi Repository để lấy một Flow mới (đã lọc)
            locationRepository.getPlacesStreamByCategory(categoryId)
        }
        .catch { e ->
            // Nếu có lỗi, phát ra danh sách rỗng và cập nhật lỗi UI
            _uiState.update { it.copy(error = e.message, isLoading = false) }
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            // Flow bắt đầu lắng nghe ngay lập tức và giữ giá trị trong 5 giây sau khi không còn người dùng
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    init {
        // [1] Lắng nghe luồng Places đã được lọc và cập nhật UiState
        viewModelScope.launch {
            placesStream.collect { filteredPlaces ->
                _uiState.update { currentState ->
                    currentState.copy(
                        // Cập nhật danh sách mới nhất
                        places = filteredPlaces,
                        isLoading = false,
                        error = null // Xóa lỗi nếu việc tải stream thành công
                    )
                }
            }
        }
        // [2] Tải thông tin người dùng
        loadUserData()
    }

    /**
     * HÀM MỚI: Gọi từ UI khi người dùng bấm vào một danh mục.
     */
    fun selectCategory(categoryId: String) {
        // Chỉ cần cập nhật StateFlow, logic lắng nghe sẽ tự động kích hoạt
        _selectedCategory.value = categoryId
        // Cần cập nhật trạng thái loading nếu danh sách quá lớn
        _uiState.update { it.copy(isLoading = true) }
    }

    // --- CÁC HÀM CŨ (Đã sửa đổi/Bỏ) ---

    // XÓA HÀM fetchPopularPlaces() CŨ (Logic đã chuyển sang placesStream)

    /**
     * Lấy thông tin người dùng hiện tại (nếu có)
     */
    fun loadUserData() {
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