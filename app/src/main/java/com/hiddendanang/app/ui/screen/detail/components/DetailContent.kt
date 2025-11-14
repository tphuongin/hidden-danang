package com.hiddendanang.app.ui.screen.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Navigation
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.components.place.PlaceCard
import com.hiddendanang.app.ui.model.DataViewModel
import com.hiddendanang.app.ui.model.Place
import com.hiddendanang.app.ui.screen.home.navToDetailScreen
import com.hiddendanang.app.ui.screen.map.MapScreen
import com.hiddendanang.app.ui.theme.Dimens

@Composable
fun DetailContent(
    navController: NavHostController,
    place: Place,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    viewModel: DataViewModel,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(place.id) {
        listState.scrollToItem(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingXLarge)
        ) {
            // Image Carousel Section
            item {
                DetailImageCarousel(
                    navController = navController,
                    images = place.images,
                    isFavorite = isFavorite,
                    onToggleFavorite = onToggleFavorite
                )
            }

            // Static Content Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.PaddingXLarge),
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingXLarge)
                ) {
                    PlaceTitleAndRating(place = place)
                    PlaceInfoSection(place = place)
                    PlaceDescription(description = place.description)

                    MapLottie()
                }
            }

            // Reviews Section
            item {
                Text(
                    text = stringResource(R.string.review),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = Dimens.PaddingXLarge)
                )
            }

            items(place.reviews.take(3)) { review ->
                ReviewCard(
                    review = review,
                    modifier = Modifier.padding(horizontal = Dimens.PaddingXLarge)
                )
            }

            // Review Actions
            item {
                ReviewActionsSection()
            }

            // Nearby Places Section
            item {
                NearbyPlacesSection(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            // Bottom Spacer
            item {
                Spacer(modifier = Modifier.height(Dimens.ButtonLarge + Dimens.PaddingMedium * 2))
            }
        }

        // --- Fixed "See Direction" button at bottom ---
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Button(
                onClick = {
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingXLarge, vertical = Dimens.PaddingLarge)
                    .height(Dimens.ButtonLarge)
                    .align(Alignment.BottomCenter)
                    .shadow(Dimens.ElevationXLarge, RoundedCornerShape(Dimens.CornerRound)),
                shape = RoundedCornerShape(Dimens.CornerRound),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                contentPadding = PaddingValues(horizontal = Dimens.SpaceLarge, vertical = Dimens.SpaceSmall)
            ) {
                Icon(
                    imageVector = Lucide.Navigation,
                    contentDescription = null,
                    modifier = Modifier
                        .size(Dimens.IconLarge)
                        .padding(end = Dimens.PaddingMedium),
                    tint = Color.White
                )
                Text(
                    text = stringResource(R.string.view_directions),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
private fun ReviewActionsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimens.PaddingXLarge,
                vertical = Dimens.PaddingMedium
            ),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        // Button view all comments
        Button(
            onClick = { /* TODO: Implement view all comments */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.ButtonMedium),
            shape = RoundedCornerShape(Dimens.PaddingXLarge),
            colors = ButtonDefaults.outlinedButtonColors()
        ) {
            Text(
                text = stringResource(R.string.see_all_reviews),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        // Button write your comment
        Button(
            onClick = { /* TODO: Implement write comment */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.ButtonLarge),
            shape = RoundedCornerShape(Dimens.CornerRound)
        ) {
            Text(
                text = stringResource(id = R.string.write_your_comment),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun NearbyPlacesSection(
    navController: NavHostController,
    viewModel: DataViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingXLarge)
    ) {
        Text(
            text = stringResource(R.string.explore_nearby_place),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = Dimens.PaddingMedium)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
            contentPadding = PaddingValues(vertical = Dimens.PaddingSmall)
        ) {
            items(viewModel.topPlace) { nearbyPlace ->
                PlaceCard(
                    modifier = Modifier.width(Dimens.CardLargeWidth),
                    place = nearbyPlace,
                    onClick = {
                        navToDetailScreen(navController, nearbyPlace.id)
                    },
                    onFavoriteToggle = { isFavorite ->
                        viewModel.toggleFavorite(nearbyPlace.id)
                    }
                )
            }
        }
    }
}

@Composable
fun MapLottie(){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.dlivery_map))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimens.PaddingLarge)
    )
}
//fun navToInteractiveMapScreen(navController: NavHostController, placeId: String){
//    navController.navigate(Screen.InteractiveMap.createRoute(placeId)){
//        popUpTo(navController.graph.startDestinationId)
//        launchSingleTop = true
//        restoreState = true
//    }
//}
