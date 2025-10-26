package com.hiddendanang.app.ui.screen.favorite.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.hiddendanang.app.ui.model.DataViewModel

@Composable
fun FavoriteContent(
    navController: NavHostController,
    viewModel: DataViewModel
) {
    val favoritePlaces by viewModel.favoritePlaces.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFavoritePlaces()
    }

    if (favoritePlaces.isEmpty()) {
        EmptyFavoriteState()
    } else {
        FavoriteList(
            places = favoritePlaces,
            navController = navController,
            viewModel = viewModel
        )
    }
}