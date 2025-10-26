package com.hiddendanang.app.ui.screen.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hiddendanang.app.ui.model.DataViewModel
import com.hiddendanang.app.ui.screen.favorite.components.FavoriteContent
import com.hiddendanang.app.ui.screen.profile.components.NotLoggedInView
import com.hiddendanang.app.ui.theme.Dimens

@Composable
fun FavoriteScreen(
    navController: NavHostController,
) {
    val viewModel: DataViewModel = viewModel()
    viewModel.loginUser("nguyenvana@email.com", "")

    val currentUser by viewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.PaddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentUser == null) {
            NotLoggedInView()
        } else {
            FavoriteContent(navController = navController, viewModel)
        }
    }
}