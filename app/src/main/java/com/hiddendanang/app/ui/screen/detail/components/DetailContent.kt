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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Navigation
import com.google.android.libraries.places.api.model.kotlin.place
import com.hiddendanang.app.R
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.model.goongmodel.Location
import com.hiddendanang.app.navigation.Screen
import com.hiddendanang.app.ui.components.place.PlaceCard
import com.hiddendanang.app.ui.model.DataViewModel
import com.hiddendanang.app.ui.screen.home.navToDetailScreen
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.viewmodel.DetailViewModel
import com.hiddendanang.app.viewmodel.GoongViewModel
import com.hiddendanang.app.utils.LocationService

@Composable
fun DetailContent(
    navController: NavHostController,
    place: Place,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    nearbyPlaces: List<Place>,
    isNearbyFavorite: (String) -> Boolean,
    onToggleNearbyFavorite: (String) -> Unit,
    viewModel: DetailViewModel,
    onWriteReviewClicked: () -> Unit,
    onRequestLocationPermission: () -> Unit = {}
) {
    val goongViewModel: GoongViewModel = viewModel()
    val context = LocalContext.current
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

            // Review Actions
            item {
                ReviewActionsSection(viewModel, onWriteReviewClicked = onWriteReviewClicked)
            }

            // Nearby Places Section
            item {
                NearbyPlacesSection(
                    navController = navController,
                    nearbyPlaces = nearbyPlaces,
                    isNearbyFavorite = isNearbyFavorite,
                    onToggleNearbyFavorite = onToggleNearbyFavorite
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
                    val destinationLocation = Location(
                        place.coordinates.latitude,
                        place.coordinates.longitude
                    )
                    navController.navigate(Screen.Map.createRoute(destinationLocation)) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                        restoreState = true
                    }
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
fun navToInteractiveMapScreen(navController: NavHostController, place: Place, destinationLocation: Location){
    val destinationLocation = Location(place.coordinates.latitude, place.coordinates.longitude)
    navController.navigate(Screen.Map.createRoute( destinationLocation)){
        popUpTo(navController.graph.startDestinationId)
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun ReviewActionsSection(
    viewModel: DetailViewModel,
    onWriteReviewClicked: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimens.PaddingXLarge,
                vertical = Dimens.PaddingMedium
            ),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        if (uiState.allReviews.isEmpty()) {
            Text(
                text = "Chưa có đánh giá nào",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            uiState.allReviews.take(2).forEach { review ->
                ReviewCard(
                    review = review,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
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
        if (uiState.isReviewFormVisible) {
            ReviewForm(
                initialRating = uiState.userReview?.rating ?: 0,
                initialComment = uiState.userReview?.comment ?: "",
                onDismiss = { viewModel.hideReviewForm() },
                onSubmit = { rating, comment ->
                    viewModel.submitUserReview(rating, comment)
                }
            )
        }
        // Button write your comment
        Button(
            onClick = { onWriteReviewClicked() },
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
    nearbyPlaces: List<Place>,
    isNearbyFavorite: (String) -> Boolean,
    onToggleNearbyFavorite: (String) -> Unit
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
            // SỬA LỖI: Dùng data thật
            items(nearbyPlaces.filter { it.id.isNotEmpty() }) { nearbyPlace ->
                PlaceCard(
                    modifier = Modifier.width(Dimens.CardLargeWidth),
                    place = nearbyPlace,
                    // SỬA LỖI: Dùng hàm được truyền vào
                    isFavorite = isNearbyFavorite(nearbyPlace.id),
                    onClick = {
                        navToDetailScreen(navController, nearbyPlace.id)
                    },
                    onFavoriteToggle = {
                        onToggleNearbyFavorite(nearbyPlace.id)
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
