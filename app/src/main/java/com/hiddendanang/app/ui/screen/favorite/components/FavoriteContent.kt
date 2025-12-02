package com.hiddendanang.app.ui.screen.favorite.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hiddendanang.app.viewmodel.FavoritesViewModel

@Composable
fun FavoriteContent(
    navController: NavHostController,
    viewModel: FavoritesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 4. Xử lý các trạng thái
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.error != null) {
            Text(text = "Đã xảy ra lỗi: ${uiState.error}")
            // TODO: Hiển thị Snackbar hoặc thông báo lỗi
        } else if (uiState.favoritePlaces.isEmpty()) {
            EmptyFavoriteState()
        } else {
            FavoriteList(
                places = uiState.favoritePlaces, // 5. Truyền list
                navController = navController,
                viewModel = viewModel // 6. Truyền VM
            )
        }
    }
}