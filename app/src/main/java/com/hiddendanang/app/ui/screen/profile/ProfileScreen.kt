package com.hiddendanang.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hiddendanang.app.ui.screen.auth.ErrorDialog
import com.hiddendanang.app.ui.screen.profile.components.EditProfileDialog
import com.hiddendanang.app.ui.screen.profile.components.NotLoggedInView
import com.hiddendanang.app.ui.screen.profile.components.ProfileHeader
import com.hiddendanang.app.ui.screen.profile.components.SettingsSection
import com.hiddendanang.app.ui.screen.profile.components.ThemeSelector
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.utils.LocalThemePreference

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    //Quản lý hiển thị edit dialog
    var showEditDialog by remember { mutableStateOf(false) }
    // Xử lý Dialog
    if (showEditDialog && uiState.user != null) {
        EditProfileDialog(
            user = uiState.user!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { newName, newBio, newPhotoUrl ->
                viewModel.updateProfile(newName, newBio, newPhotoUrl)
                showEditDialog = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }
            uiState.error != null -> {
                ErrorDialog(
                    message = uiState.error!!,
                    onDismiss = { viewModel.errorShown() }
                )
            }
            uiState.user == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimens.PaddingMedium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NotLoggedInView()
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(Dimens.PaddingMedium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoggedInProfile(
                        user = uiState.user!!,
                        onLogout = { viewModel.logout() }, // Truyền sự kiện logout
                        viewModel = viewModel,
                        onEditClick = { showEditDialog = true }
                    )
                }
            }
        }
    }
}

@Composable
fun LoggedInProfile(
    user: com.hiddendanang.app.data.model.User,
    onLogout: () -> Unit, // Thêm
    viewModel: ProfileViewModel,
    onEditClick: () -> Unit
) {
    val themePreference = LocalThemePreference.current
    val favoriteCount by viewModel.favoriteCount.collectAsState()
    ProfileHeader(
        user = user,
        favoriteCount = favoriteCount,
        reviewsCount = user.stats.reviews_count,
        visitedCount = user.stats.locations_added,
        onEditClick = onEditClick
    )
    Spacer(Modifier.height(Dimens.SpaceMedium))

    ThemeSelector(
        currentTheme = themePreference.value,
        onThemeChange = { newTheme ->
            // 1. Cập nhật state UI ngay lập tức (để nhìn thấy đổi màu)
            themePreference.value = newTheme

            // 2. Báo ViewModel lưu lại (vào máy + lên mạng)
            viewModel.onThemeChange(newTheme)
        }
    )

    Spacer(Modifier.height(Dimens.SpaceMedium))

    SettingsSection(
        onLogout = onLogout
    )
}