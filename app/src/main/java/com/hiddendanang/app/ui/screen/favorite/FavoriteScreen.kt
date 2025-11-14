package com.hiddendanang.app.ui.screen.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.hiddendanang.app.ui.screen.favorite.components.FavoriteContent
import com.hiddendanang.app.ui.screen.profile.components.NotLoggedInView
import com.hiddendanang.app.ui.theme.Dimens

@Composable
fun FavoriteScreen(
    navController: NavHostController,
) {
    val viewModel: FavoritesViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.PaddingMedium),
        contentAlignment = Alignment.Center // Căn giữa
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }
            // 4. Kiểm tra thông báo lỗi để biết chưa đăng nhập
            uiState.error != null && uiState.error!!.contains("User not logged in") -> {
                Column(
                    modifier = Modifier.fillMaxSize(), // Cần Column để NotLoggedInView căn giữa
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NotLoggedInView()
                }
                // Bạn có thể muốn gọi viewModel.errorShown() ở đây
            }
            else -> {
                // Đã đăng nhập và có data (hoặc list rỗng)
                FavoriteContent(navController = navController, viewModel)
            }
        }
    }
}