package com.hiddendanang.app.ui.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hiddendanang.app.ui.model.DataViewModel
import com.hiddendanang.app.ui.screen.detail.components.DetailContent

@Composable
fun DetailScreen(
    navController: NavHostController,
    placeId: String,
) {
    val viewModel: DataViewModel = viewModel()
    LaunchedEffect(placeId) {
        viewModel.getPlaceById(placeId)
    }
    val place by viewModel.selectedPlace.collectAsState()
    val isFavorite by viewModel.isFavorite(placeId).collectAsState(initial = false)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        place?.let { currentPlace ->
            DetailContent(
                navController = navController,
                place = currentPlace,
                isFavorite = isFavorite,
                onToggleFavorite = { viewModel.toggleFavorite(currentPlace.id) },
                viewModel = viewModel,
            )
        } ?: run {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
