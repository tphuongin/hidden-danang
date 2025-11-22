package com.hiddendanang.app.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hiddendanang.app.data.model.User
import com.hiddendanang.app.data.repository.AuthRepository
import com.hiddendanang.app.utils.constants.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Lớp sealed này đại diện cho tất cả các trạng thái có thể có của màn hình Auth.
 * UI sẽ "lắng nghe" state này để biết khi nào cần hiển thị gì.
 */
sealed class AuthUiState {
    object Idle : AuthUiState()      // Trạng thái chờ, mặc định
    object Loading : AuthUiState()   // Đang tải (hiện vòng xoay)
    object Success : AuthUiState()   // Thành công (để điều hướng sang trang Home)
    data class Error(val message: String) : AuthUiState() // Có lỗi (hiện thông báo)
}

class AuthViewModel : ViewModel() {

    // Khởi tạo Repository
    private val authRepository: AuthRepository = AuthRepository()

    // StateFlow để giữ trạng thái UI (Idle, Loading, Success, Error)
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    //TRẠNG THÁI XÁC THỰC
    private val _isLoggedIn = MutableStateFlow(authRepository.getCurrentUser() != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()
    /**
     * Hàm này được gọi từ LoginScreen khi người dùng nhấn nút "Login".
     */
    fun login(email: String, password: String) {
        // Kiểm tra đầu vào đơn giản
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Vui lòng nhập email và mật khẩu")
            return
        }

        // Chạy coroutine trong ViewModel
        viewModelScope.launch {
            // B1: Cập nhật trạng thái sang Loading
            _uiState.value = AuthUiState.Loading

            // B2: Gọi hàm trong repository
            val result = authRepository.loginUser(email, password)

            // B3: Xử lý kết quả trả về
            result.fold(
                onSuccess = { user ->
                    val userTheme = user.preferences?.theme
                    if (userTheme != null) {
                        try {
                            val mode = AppThemeMode.valueOf(userTheme)
                        } catch (e: Exception) {
                            // Ignore lỗi format
                        }
                    }
                    // Nếu thành công, cập nhật trạng thái
                    _uiState.value = AuthUiState.Success
                    _isLoggedIn.value = true
                },
                onFailure = { e ->
                    // Nếu thất bại, gửi thông báo lỗi
                    _uiState.value = AuthUiState.Error(e.message ?: "Có lỗi xảy ra")
                }
            )
        }
    }

    /**
     * Hàm này được gọi từ RegisterScreen khi người dùng nhấn nút "Sign Up".
     *
     * QUAN TRỌNG: Hàm này cần `fullName`, `email`, `password`.
     * Bạn cần sửa file `AuthRepository.kt` một chút để chấp nhận `fullName`.
     * (Xem hướng dẫn ở mục 2)
     */
    fun register(fullName: String, email: String, password: String, confirmPassword: String) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _uiState.value = AuthUiState.Error("Vui lòng nhập đầy đủ thông tin")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = AuthUiState.Error("Mật khẩu không khớp")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            // Gọi hàm repository ĐÃ SỬA ĐỔI
            val result = authRepository.registerUser(fullName, email, password)

            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState.Success
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Đăng ký thất bại")
                }
            )
        }
    }

    fun getUserById(userId: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = authRepository.getUserById(userId)

            result.fold(
                onSuccess = { userData ->
                    _user.value = userData
                    _uiState.value = AuthUiState.Success
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Không thể tải thông tin người dùng")
                }
            )
        }
    }


    /**
     * Dùng để reset trạng thái về Idle (ví dụ: sau khi người dùng đóng dialog lỗi)
     */
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}