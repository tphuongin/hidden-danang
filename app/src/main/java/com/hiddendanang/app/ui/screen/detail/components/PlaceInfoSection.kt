package com.hiddendanang.app.ui.screen.detail.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.composables.icons.lucide.Clock3
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPinHouse
import com.composables.icons.lucide.Route
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.Wallet
import com.composables.icons.lucide.Waypoints
import com.hiddendanang.app.R
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.utils.LocationService
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PlaceTitleAndRating(place: Place) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        Text(
            text = place.name,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rating and Review Count
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingNano),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Lucide.Star,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.IconSmall),
                    tint = Color.Yellow
                )
                Text(
                    text = "${place.rating_summary.average}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = ". ${place.rating_summary.count} ${stringResource(R.string.review)} ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            // Category Tag
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(Dimens.CornerRound)
                    )
                    .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingTiny)
            ) {
                Text(
                    text = place.subcategory,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun PlaceInfoSection(place: Place, currentLocationLat: Double? = null, currentLocationLng: Double? = null) {
    val context = LocalContext.current
    val locationService = LocationService(context)
    
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        // Address
        val addressDisplay = place.address.formatted_address.ifEmpty {
            stringResource(R.string.being_updated)
        }
        InfoItem(Lucide.MapPinHouse, R.string.address, addressDisplay)
        
        // Opening Hours Logic
        val hoursText = if (place.opening_hours != null) {
             val today = place.opening_hours.mon 
             if (today != null) {
                 if (today.open == "00:00" && today.close == "23:59") stringResource(R.string.open_24_7)
                 else if (today.isClosed) stringResource(R.string.closed)
                 else "${today.open} - ${today.close}"
             } else stringResource(R.string.being_updated)
        } else stringResource(R.string.being_updated)
        
        InfoItem(Lucide.Clock3, R.string.opening_hours, hoursText)

        // Price Logic - hiển thị "Đang cập nhật" vì price_range = null và price_indicator rỗng
        val priceText = if (place.price_range != null && (place.price_range.min > 0 || place.price_range.max > 0)) {
             val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
             try {
                 "${format.format(place.price_range.min.toInt())} - ${format.format(place.price_range.max.toInt())} ${place.price_range.currency}"
             } catch (e: Exception) {
                 stringResource(R.string.being_updated)
             }
        } else stringResource(R.string.being_updated)
        
        InfoItem(Lucide.Wallet, R.string.price_range, priceText)

        // Distance Logic - tính khoảng cách từ vị trí hiện tại sử dụng hàm từ LocationService
        val distanceText = if (currentLocationLat != null && currentLocationLng != null) {
            val distance = locationService.calculateDistance(
                currentLocationLat, currentLocationLng,
                place.coordinates.latitude, place.coordinates.longitude
            )
            "~${String.format("%.1f", distance)} km"
        } else {
            stringResource(R.string.being_updated)
        }
        
        InfoItem(Lucide.Waypoints, R.string.distance, distanceText)
    }
}

@Composable
fun PlaceDescription(description: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        Text(
            text = stringResource(R.string.introduce),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun MapCard(place: Place) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.ContainerMedium),
        shape = RoundedCornerShape(Dimens.PaddingXLarge)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Map image placeholder
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(place.images.getOrNull(1) ?: place.images.firstOrNull())
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.error),
                contentDescription = "Map",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Location Pin Icon
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(Dimens.ButtonLarge)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.Route,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(Dimens.IconLarge)
                )
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: Int, value: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.IconSmall)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingTiny)
        ) {
            Text(
                text = stringResource(id = label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}