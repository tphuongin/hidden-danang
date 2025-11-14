package com.hiddendanang.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hiddendanang.app.data.model.User
import com.hiddendanang.app.data.repository.AuthRepository
import com.hiddendanang.app.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Trạng thái (State) cho giao diện ProfileScreen
data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null, // Dùng model User (từ Firestore)
    val error: String? = null
)

class ProfileViewModel : ViewModel() {
    private val authRepository: AuthRepository = AuthRepository()
    private val favoritesRepository: FavoritesRepository by lazy { FavoritesRepository() }


    // StateFlow riêng cho ProfileScreen
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // Bắt đầu lắng nghe ngay lập tức
        loadUserProfile()
    }

    val favoriteCount: StateFlow<Int> =
        favoritesRepository.getFavoriteIdsStream()
            .map { it.size } // biến Set<String> thành số lượng phần tử
            .catch { emit(0) } // nếu lỗi thì trả 0
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    private fun loadUserProfile() {
        viewModelScope.launch {
            // B1: Lắng nghe trạng thái Auth (currentUserStream)
            authRepository.currentUserStream
                .flatMapLatest { firebaseUser ->
                    // flatMapLatest: Khi firebaseUser thay đổi (vd: logout),
                    // nó sẽ hủy Flow cũ và chạy Flow mới.
                    if (firebaseUser != null) {
                        // B2: Nếu user đăng nhập, lấy UID và lắng nghe Profile
                        authRepository.getUserProfileStream(firebaseUser.uid)
                    } else {
                        // B3: Nếu user đăng xuất, phát ra (emit) null
                        flowOf(null)
                    }
                }
                .catch { e ->
                    // Xử lý lỗi nếu không thể get profile
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { userProfile ->
                    // B4: Cập nhật UI với data (User) từ Firestore
                    _uiState.update {
                        it.copy(isLoading = false, user = userProfile, error = null)
                    }
                }
        }
    }

    /**
     * Hàm này được gọi từ nút Logout trên ProfileScreen
     */
    fun logout() {
        authRepository.logout()
        // `MainViewModel` sẽ tự động bắt sự kiện này và điều hướng
    }
    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }
}
