package com.hiddendanang.app.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.hiddendanang.app.ui.screen.home.components.SearchBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hiddendanang.app.R
import com.hiddendanang.app.navigation.Screen
import com.hiddendanang.app.ui.components.place.PlaceCard
import com.hiddendanang.app.ui.screen.auth.ErrorDialog
import com.hiddendanang.app.ui.screen.auth.FullScreenLoading
import com.hiddendanang.app.viewmodel.FavoritesViewModel
import com.hiddendanang.app.ui.screen.home.components.CategoryRow
import com.hiddendanang.app.ui.screen.home.components.HomePageTitle
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.viewmodel.HomeViewModel

@Composable
fun HomePageScreen(navController: NavHostController) {
    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(vertical = Dimens.PaddingLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            // --- Header ---
            item {
                Column(Modifier.padding(horizontal = Dimens.PaddingLarge)) {
                    HomePageTitle()
                    Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                    SearchBar(
                        onClick = {
                            // Điều hướng đến màn hình Search mới
                            navController.navigate(Screen.Search.route)
                        }
                    )
                    Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                    CategoryRow(
                        currentCategory = selectedCategory, // Truyền trạng thái hiện tại
                        onCategorySelected = viewModel::selectCategory // Truyền hàm xử lý sự kiện
                    )
                }
            }

            // --- Featured Places ---
            item {
                Column(Modifier.padding(horizontal = Dimens.PaddingLarge)) {
                    Text(
                        text = stringResource(R.string.featured_location),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = Dimens.PaddingLarge),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
                ) {
                    items(uiState.places) { place ->
                        PlaceCard(
                            modifier = Modifier.width(Dimens.CardLargeWidth),
                            place = place,
                            isFavorite = place.id in favoriteIds,
                            onClick = { navToDetailScreen(navController, place.id) },
                            onFavoriteToggle = { favoritesViewModel.toggleFavorite(place) }
                        )
                    }
                }
            }

            // --- Explore more ---
            item {
                Column(Modifier.padding(horizontal = Dimens.PaddingLarge)) {
                    Text(
                        text = stringResource(R.string.discover_more),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            items(uiState.places.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.PaddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
                ) {
                    rowItems.forEach { place ->
                        PlaceCard(
                            modifier = Modifier.weight(Dimens.WeightMedium),
                            place = place,
                            place.id in favoriteIds,
                            onClick = { navToDetailScreen(navController, place.id) },
                            onFavoriteToggle = { favoritesViewModel.toggleFavorite(place) }
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(Dimens.WeightMedium))
                    }
                }
            }
        }
        if (uiState.isLoading) {
            FullScreenLoading()
        }

        uiState.error?.let { message ->
            ErrorDialog(message = message, onDismiss = { viewModel.errorShown() })
        }
    }
}
fun navToDetailScreen(navController: NavHostController, placeId: String){
    navController.navigate(Screen.DetailPlace.createRoute(placeId)){
        launchSingleTop = true
        restoreState = true
        popUpTo(Screen.DetailPlace.route) {
            inclusive = false
        }
    }
}
