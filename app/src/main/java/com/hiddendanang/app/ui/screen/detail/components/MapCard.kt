package com.hiddendanang.app.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPinOff
import com.composables.icons.lucide.Route
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.model.Place
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.viewmodel.GoongViewModel

@SuppressLint("LocalContextResourcesRead")
@Composable
fun MapCard(
    place: Place,
    onRequestLocationPermission: () -> Unit = {},
    viewModel: GoongViewModel = viewModel()
) {
    val context = LocalContext.current
    val bitmap by viewModel.staticMapBitmap.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(place, currentLocation) {
        if (currentLocation != null) {
            // Có location, fetch static map
            val destination = "${place.latitude},${place.longitude}"
            viewModel.fetchStaticMap(
                origin = currentLocation!!,
                destination = destination,
                width = context.resources.displayMetrics.widthPixels,
                height = (context.resources.displayMetrics.widthPixels * 0.6).toInt()
            )
        } else {
            // Chưa có location, thử lấy nếu có quyền
            if (viewModel.hasLocationPermission()) {
                viewModel.fetchCurrentLocation()
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.ContainerMedium),
        shape = RoundedCornerShape(Dimens.PaddingXLarge)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                bitmap != null -> {
                    // Hiển thị static map
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = stringResource(R.string.map_card_static_map_description),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                errorMessage != null -> {
                    // Hiển thị lỗi với khả năng retry
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                // Retry logic
                                if (viewModel.hasLocationPermission()) {
                                    viewModel.fetchCurrentLocation()
                                } else {
                                    onRequestLocationPermission()
                                }
                            }
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Lucide.MapPinOff,
                                contentDescription = stringResource(R.string.map_card_location_error_description),
                                modifier = Modifier.size(Dimens.IconXLarge),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = stringResource(R.string.map_card_load_error_title),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = Dimens.PaddingSmall)
                            )
                            Text(
                                text = stringResource(R.string.map_card_load_error_retry),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = Dimens.PaddingTiny)
                            )
                        }
                    }
                }
                !viewModel.hasLocationPermission() -> {
                    // Chưa có quyền location - hiển thị UI yêu cầu quyền
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onRequestLocationPermission() }
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Lucide.MapPinOff,
                                contentDescription = stringResource(R.string.map_card_permission_required_description),
                                modifier = Modifier.size(Dimens.IconXLarge),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.map_card_permission_required_title),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = Dimens.PaddingSmall)
                            )
                            Text(
                                text = stringResource(R.string.map_card_permission_required_action),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = Dimens.PaddingTiny)
                            )
                        }
                    }
                }
                bitmap == null && viewModel.hasLocationPermission() -> {
                    // Đang loading location (chỉ khi có quyền)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(Dimens.IconLarge),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.map_card_loading),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = Dimens.PaddingMedium)
                            )
                        }
                    }
                }
                else -> {
                    // Fallback: hiển thị ảnh placeholder từ place
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(place.images.getOrNull(1) ?: place.images.firstOrNull())
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.placeholder),
                        error = painterResource(R.drawable.error),
                        contentDescription = stringResource(R.string.map_card_place_image_description),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Location Pin Icon (chỉ hiển thị khi có static map)
            if (bitmap != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(Dimens.IconXLarge)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Route,
                        contentDescription = stringResource(R.string.map_card_route_pin_description),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(Dimens.IconMedium)
                    )
                }
            }
        }
    }
}
