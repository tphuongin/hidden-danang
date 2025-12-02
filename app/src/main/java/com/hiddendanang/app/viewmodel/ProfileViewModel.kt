package com.hiddendanang.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hiddendanang.app.data.model.User
import com.hiddendanang.app.data.repository.AuthRepository
import com.hiddendanang.app.data.repository.FavoritesRepository
import com.hiddendanang.app.data.repository.ReviewRepository
import com.hiddendanang.app.utils.constants.AppThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private val reviewRepository: ReviewRepository by lazy { ReviewRepository() }
    private var saveThemeJob: Job? = null

    // StateFlow riêng cho ProfileScreen
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // Bắt đầu lắng nghe ngay lập tức
        loadUserProfile()
    }

    fun onThemeChange(newTheme: AppThemeMode) {
//        // 1. Cập nhật UI NGAY LẬP TỨC (để user thấy app mượt)
//        // Cập nhật LocalThemePreference (DataStore) ở đây để lần sau vào app load cho nhanh
//        viewModelScope.launch {
//            userPreferencesRepository.saveThemePreference(newTheme)
//        }

        // 2. Hủy job cũ nếu user bấm liên tục
        saveThemeJob?.cancel()

        // 3. Tạo job mới với độ trễ (Debounce)
        saveThemeJob = viewModelScope.launch {
            // Chờ 2 giây (hoặc 3s). Nếu trong 2s này user bấm tiếp, dòng này sẽ bị cancel
            delay(3000)

            // 4. Sau 3s user không bấm gì nữa -> Mới gọi Firestore
            saveThemeToFirestore(newTheme)
        }
    }

    private suspend fun saveThemeToFirestore(theme: AppThemeMode) {
        val uid = authRepository.getCurrentUser()?.uid ?: return
        try {
            authRepository.updateUserProfile(
                uid = uid,
                theme = theme.name // Chuyển enum thành String: "LIGHT", "DARK"...
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                    // Handle error if unable to get profile
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { userProfile ->
                    // B4: Cập nhật UI với data
                    _uiState.update {
                        it.copy(isLoading = false, user = userProfile, error = null)
                    }
                }
        }
    }

    // Hàm update profile
    fun updateProfile(newName: String, newBio: String, newPhotoUrl: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val uid = authRepository.getCurrentUser()?.uid
            if (uid != null) {
                // Gọi hàm update vừa viết
                val result = authRepository.updateUserProfile(
                    uid = uid,
                    displayName = newName,
                    bio = newBio,
                    photoUrl = newPhotoUrl
                )

                if (result.isFailure) {
                    _uiState.update { it.copy(error = "Cập nhật thất bại", isLoading = false) }
                }
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Hàm này được gọi từ nút Logout trên ProfileScreen
    fun logout() {
        authRepository.logout()
    }

    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }
}
