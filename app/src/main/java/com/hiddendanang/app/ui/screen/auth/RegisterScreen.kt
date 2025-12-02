package com.hiddendanang.app.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import com.composables.icons.lucide.Lucide
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.hiddendanang.app.R
import com.hiddendanang.app.navigation.Screen
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.viewmodel.AuthUiState
import com.hiddendanang.app.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(navController: NavHostController) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val viewModel: AuthViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is AuthUiState.Success -> {
            SuccessDialog(
                onDismiss = {
                    viewModel.resetState()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        is AuthUiState.Error -> {
            ErrorDialog(
                message = state.message,
                onDismiss = {
                    viewModel.resetState()
                }
            )
        }

        is AuthUiState.Loading -> {
            // (Không cần làm gì, button và textfield sẽ tự bị vô hiệu hóa)
        }

        is AuthUiState.Idle -> {
            // Không làm gì cả
        }

        is AuthUiState.Error -> TODO()
        AuthUiState.Idle -> TODO()
        AuthUiState.Loading -> TODO()
        AuthUiState.Success -> TODO()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
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
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Sign up to get started",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                // --- Trường Tên đầy đủ ---
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = (uiState !is AuthUiState.Loading) // <-- SỬA 1
                )

                // --- Trường Email ---
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = (uiState !is AuthUiState.Loading) // <-- SỬA 2
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
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, null)
                        }
                    },
                    enabled = (uiState !is AuthUiState.Loading) // <-- SỬA 3
                )

                // --- Trường Xác nhận Mật khẩu ---
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    enabled = (uiState !is AuthUiState.Loading) // <-- SỬA 4
                )

                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

                // --- Nút Đăng ký ---
                Button(
                    onClick = {
                        // <-- SỬA 5: Truyền 4 tham số
                        viewModel.register(fullName, email, password, confirmPassword)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.ButtonMedium),
                    enabled = (uiState !is AuthUiState.Loading) // <-- Đã có
                ) {
                    if (uiState is AuthUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(Dimens.IconSmall))
                    } else {
                        Text(text = "Sign Up", style = MaterialTheme.typography.titleMedium)
                    }
                }

                // --- Link đến trang Đăng nhập ---
                TextButton(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    enabled = (uiState !is AuthUiState.Loading) // <-- SỬA 6
                ) {
                    Text(
                        text = "Already have an account? Log In",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable

fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lỗi") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun SuccessDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thành công") },
        text = { Text("Tài khoản của bạn đã được tạo thành công. Vui lòng đăng nhập.") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.95f))
            .clickable(enabled = false, onClick = {}), // Chặn click
        contentAlignment = Alignment.Center
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )
    }
}