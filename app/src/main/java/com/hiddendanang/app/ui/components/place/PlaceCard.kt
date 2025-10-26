package com.hiddendanang.app.ui.components.place

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.model.DataViewModel
import com.hiddendanang.app.ui.model.Place
import com.hiddendanang.app.ui.theme.Dimens

@Composable
fun PlaceCard(
    modifier: Modifier = Modifier,
    place: Place,
    onClick: ((placeId: String) -> Unit)? = null,
    onFavoriteToggle: ((Boolean) -> Unit)? = null
) {
    val viewModel: DataViewModel = viewModel()
    val isFavorite by viewModel.isFavorite(place.id).collectAsState(initial = false)
    var isPressed by remember { mutableStateOf(false) }

    val elevation by animateDpAsState(
        targetValue = if (isPressed) Dimens.ElevationPressed else Dimens.ElevationMedium,
        animationSpec = tween(durationMillis = 150), label = ""
    )
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.13f else 1f,
        animationSpec = tween(durationMillis = 150), label = ""
    )
    val heartScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = ""
    )

    Card(
        shape = RoundedCornerShape(Dimens.CornerXLarge),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .height(Dimens.ContainerMedium)
            .padding(Dimens.PaddingSmall)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick?.invoke(place.id) }
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .height(Dimens.CardMinHeight)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = Dimens.CornerXLarge, topEnd = Dimens.CornerXLarge))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(place.images.firstOrNull() ?: place.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = place.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(R.drawable.error)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Dimens.PaddingSmall)
                        .background(
                            color = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                            shape = CircleShape
                        )
                        .size(Dimens.IconLargeMedium)
                        .clickable {
                            viewModel.toggleFavorite(place.id)
                            onFavoriteToggle?.invoke(!isFavorite)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Heart,
                        contentDescription = stringResource(R.string.favorite),
                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.graphicsLayer(
                            scaleX = heartScale,
                            scaleY = heartScale
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

            Column(
                modifier = Modifier
                    .padding(horizontal = Dimens.PaddingMedium)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = place.name,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = place.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingTiny),
                    modifier = Modifier.padding(bottom = Dimens.PaddingSmall)
                ) {
                    Icon(
                        imageVector = Lucide.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${place.rating}",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
