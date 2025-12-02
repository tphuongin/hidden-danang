package com.hiddendanang.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.hiddendanang.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel cấp cao nhất (toàn cục) của ứng dụng.
 * Nhiệm vụ chính là cung cấp trạng thái đăng nhập cho toàn bộ app.
 */
class MainViewModel : ViewModel() {
    private val authRepository: AuthRepository = AuthRepository()

    /**
     * Một StateFlow phát ra User (nếu đăng nhập) hoặc null (nếu đăng xuất).
     * Bất kỳ Composable/ViewModel nào cũng có thể lắng nghe Flow này
     * để biết "ai đang đăng nhập".
     *
     * stateIn(...) biến Flow "lạnh" thành "nóng", giữ giá trị cuối cùng
     * và chia sẻ nó cho tất cả các listener.
     */
    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUserStream
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Bắt đầu khi có listener
            initialValue = null // Ban đầu là chưa biết (null)
        )
}

