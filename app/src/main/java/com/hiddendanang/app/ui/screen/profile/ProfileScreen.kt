package com.hiddendanang.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hiddendanang.app.ui.model.DataViewModel
import com.hiddendanang.app.ui.model.User
import com.hiddendanang.app.ui.screen.profile.components.NotLoggedInView
import com.hiddendanang.app.ui.screen.profile.components.ProfileHeader
import com.hiddendanang.app.ui.screen.profile.components.SettingsSection
import com.hiddendanang.app.ui.screen.profile.components.ThemeSelector
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.utils.LocalThemePreference

@Composable
fun ProfileScreen(
    navController: NavHostController,
) {
    val viewModel: DataViewModel = viewModel()
    viewModel.loginUser("nguyenvana@email.com", "")
    val currentUser by viewModel.currentUser.collectAsState()
    val (favoriteCount, reviewCount, visitedCount) = remember {
        viewModel.getCurrentUserStats()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.PaddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentUser == null) {
            NotLoggedInView()
        } else {
            LoggedInProfile(
                user = currentUser!!,
                favoriteCount = favoriteCount,
                reviewsCount = reviewCount,
                visitedCount = visitedCount
            )
        }
    }
}

@Composable
fun LoggedInProfile(
    user: User,
    favoriteCount: Int = 0,
    reviewsCount: Int = 0,
    visitedCount: Int = 0
) {
    val themePreference = LocalThemePreference.current

    ProfileHeader(
        user = user,
        favoriteCount = favoriteCount,
        reviewsCount = reviewsCount,
        visitedCount = visitedCount
    )
    Spacer(Modifier.height(Dimens.SpaceMedium))

    ThemeSelector(
        currentTheme = themePreference.value,
        onThemeChange = { themePreference.value = it }
    )

    Spacer(Modifier.height(Dimens.SpaceMedium))

    SettingsSection(
        onLogout = {
            // TODO: Implement logout logic
        }
    )
}
