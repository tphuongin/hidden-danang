package com.hiddendanang.app.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import com.composables.icons.lucide.Lucide
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel // Thêm import
import androidx.navigation.NavHostController
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.hiddendanang.app.navigation.Screen
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.viewmodel.AuthUiState
import com.hiddendanang.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavHostController) {
    // 1. Quản lý trạng thái cho các trường nhập liệu
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // 2. Khởi tạo ViewModel và lắng nghe State
    val viewModel: AuthViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // 3. Lắng nghe trạng thái Success để tự động điều hướng
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            // Đăng nhập thành công, chuyển đến trang Home
            // Xóa toàn bộ backstack (Splash, Login, Register)
            navController.navigate(Screen.HomePage.route) { // Thay bằng route Home
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // 4. Gói mọi thứ vào Box để hiển thị overlay (Loading, Dialog)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 5. Giao diện chính (Form đăng nhập)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimens.PaddingLarge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
                ) {
                    // --- Tiêu đề ---
                    Text(
                        text = "Welcome Back!",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Login to continue",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                    // --- Trường Email ---
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = (uiState !is AuthUiState.Loading) // Vô hiệu hóa khi tải
                    )

                    // --- Trường Mật khẩu ---
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Lucide.Eye else Lucide.EyeOff
                            val description = if (passwordVisible) "Hide password" else "Show password"
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, description)
                            }
                        },
                        enabled = (uiState !is AuthUiState.Loading) // Vô hiệu hóa khi tải
                    )

                    Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                    // --- Nút Đăng nhập ---
                    Button(
                        onClick = {
                            // Gọi hàm login từ ViewModel
                            viewModel.login(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.ButtonMedium),
                        enabled = (uiState !is AuthUiState.Loading) // Vô hiệu hóa khi tải
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(Dimens.IconSmall))
                        } else {
                            Text(text = "Sign Up", style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    // --- Link đến trang Đăng ký ---
                    TextButton(
                        onClick = {
                            navController.navigate(Screen.Register.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        enabled = (uiState !is AuthUiState.Loading) // Vô hiệu hóa khi tải
                    ) {
                        Text(
                            text = "Don't have an account? Sign Up",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // 6. Xử lý các trạng thái Overlay (Loading, Error)
        when (val state = uiState) {
            is AuthUiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = { viewModel.resetState() }
                )
            }
            is AuthUiState.Loading -> {
            }
            else -> {}
        }
    }
}