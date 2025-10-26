package com.hiddendanang.app.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.hiddendanang.app.ui.screen.home.components.SearchBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hiddendanang.app.R
import com.hiddendanang.app.navigation.Screen
import com.hiddendanang.app.ui.components.place.PlaceCard
import com.hiddendanang.app.ui.model.DataViewModel
import com.hiddendanang.app.ui.screen.home.components.CategoryRow
import com.hiddendanang.app.ui.screen.home.components.HomePageTitle
import com.hiddendanang.app.ui.theme.Dimens

@Composable
fun HomePageScreen(navController: NavHostController) {
    val viewmodel: DataViewModel = viewModel()
    val featuredPlaces = viewmodel.topPlace
    val exploreMore = viewmodel.topPlace
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
                SearchBar()
                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                CategoryRow()
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
                items(featuredPlaces) { place ->
                    PlaceCard(
                        modifier = Modifier.width(Dimens.CardLargeWidth),
                        place = place,
                        onClick = { navToDetailScreen(navController, place.id) }
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

        items(exploreMore.chunked(2)) { rowItems ->
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
                        onClick = { navToDetailScreen(navController, place.id) }
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(Dimens.WeightMedium))
                }
            }
        }
    }
}
fun navToDetailScreen(navController: NavHostController, placeId: String){
    navController.navigate(Screen.DetailPlace.createRoute(placeId)){
        popUpTo(navController.graph.startDestinationId)
        launchSingleTop = true
        restoreState = true
    }
}
