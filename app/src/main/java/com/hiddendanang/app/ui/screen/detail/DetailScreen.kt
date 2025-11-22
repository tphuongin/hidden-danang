package com.hiddendanang.app.ui.screen.detail

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.libraries.places.api.model.kotlin.place
import com.hiddendanang.app.navigation.Screen
import com.hiddendanang.app.ui.screen.auth.AuthViewModel
import com.hiddendanang.app.ui.screen.auth.ErrorDialog
import com.hiddendanang.app.ui.screen.auth.FullScreenLoading
import com.hiddendanang.app.ui.screen.detail.components.DetailContent


@Composable
fun DetailScreen(
    navController: NavHostController,
    placeId: String,
    onRequestLocationPermission: () -> Unit = {}
) {
    val viewModel: DetailViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    val onWriteReviewClicked = {
        Log.d("DETAIL_SCREEN", "isLoggedIn status: $isLoggedIn")
         //[1] Lấy trạng thái đăng nhập từ ViewModel (Giả định AuthViewModel cung cấp trạng thái)
        if (isLoggedIn) {
            viewModel.showReviewForm()
        } else {
            // [2] Thực hiện điều hướng
            navController.navigate(Screen.Login.route)
        }
    }
    LaunchedEffect(placeId) {
        viewModel.listenToDataChanges(placeId, true)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                FullScreenLoading()
            }
            uiState.error != null -> {
                ErrorDialog(
                    message = uiState.error!!,
                    onDismiss = { viewModel.errorShown() }
                )
            }
            uiState.place != null -> {
                // Chỉ hiển thị Content khi data đã sẵn sàng
                DetailContent(
                    navController = navController,
                    place = uiState.place!!, // An toàn vì đã check != null
                    isFavorite = uiState.isFavorite,
                    onToggleFavorite = { viewModel.toggleFavorite(uiState.place!!) }, // Truyền tham chiếu hàm

                    // SỬA LỖI: Truyền data Lân cận
                    nearbyPlaces = uiState.nearbyPlaces,

                    // SỬA LỖI: Truyền các hàm Yêu thích (tạm thời)
                    isNearbyFavorite = { nearbyPlaceId ->
                        nearbyPlaceId in uiState.favoriteIds
                    },
                    onToggleNearbyFavorite = { nearbyPlaceId -> // nearbyPlaceId là một String

                        // 1. Tìm đối tượng Place đầy đủ từ list lân cận
                        val placeToToggle = uiState.nearbyPlaces.find { it.id == nearbyPlaceId }

                        // 2. Nếu tìm thấy, mới gọi ViewModel
                        if (placeToToggle != null) {
                            viewModel.toggleFavorite(placeToToggle)
                        }
                    },
                    viewModel = viewModel,
                    onRequestLocationPermission = onRequestLocationPermission,
                    onWriteReviewClicked = onWriteReviewClicked
                )
            }
        }
    }
}
