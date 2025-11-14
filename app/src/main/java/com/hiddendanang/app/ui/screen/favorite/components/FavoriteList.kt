package com.hiddendanang.app.ui.screen.favorite.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.hiddendanang.app.R
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.ui.components.place.PlaceCard
import com.hiddendanang.app.viewmodel.FavoritesViewModel
import com.hiddendanang.app.ui.screen.home.navToDetailScreen
import com.hiddendanang.app.ui.theme.Dimens

@Composable
fun FavoriteList(
    places: List<Place>,
    navController: NavHostController,
    viewModel: FavoritesViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.PaddingLarge),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        item {
            Text(
                text = stringResource(R.string.my_favorite),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = Dimens.PaddingSmall)
            )
        }

        items(
            items = places,
            key = { it.id } // Thêm key để optimization
        ) { place ->
            PlaceCard(
                modifier = Modifier.fillMaxWidth(),
                place = place,
                true,
                onClick = {
                    navToDetailScreen(navController, place.id)
                },
                onFavoriteToggle = {
                    // Không cần tham số 'isFavorite' hay 'if'
                    // Một cú click ở đây luôn có nghĩa là "bỏ yêu thích"
                    viewModel.removeFromFavorites(place.id)
                },
            )
        }

        item {
            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
        }
    }
}