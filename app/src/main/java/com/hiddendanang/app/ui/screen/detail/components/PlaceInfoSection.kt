package com.hiddendanang.app.ui.screen.detail.components

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
import com.hiddendanang.app.data.model.OpeningHours
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.model.PriceRange
import com.hiddendanang.app.ui.theme.Dimens
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


fun getCurrentDayTime(openingHours: OpeningHours): String {
    // Logic đơn giản: Lấy giờ của Thứ Hai làm ví dụ.
    // Trong thực tế, bạn cần xác định ngày hiện tại.
    val today = openingHours.mon // Giả sử là Thứ Hai

    return if (today.is_closed) {
        "Đóng cửa"
    } else {
        "${today.open} - ${today.close}"
    }
}

fun formatPriceRange(priceRange: PriceRange): String {
    if (priceRange.min == 0 && priceRange.max == 0) return "Không có thông tin giá"

    // Định dạng số tiền (ví dụ: 20,000 - 50,000 VND)
    val minFormatted = String.format("%,d", priceRange.min)
    val maxFormatted = String.format("%,d", priceRange.max)

    return "$minFormatted - $maxFormatted ${priceRange.currency}"
}

@Composable
fun PlaceInfoSection(place: Place) {
    val formattedOpeningHours = getCurrentDayTime(place.opening_hours)
    val formattedPriceRange = formatPriceRange(place.price_range_detail)
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        // Address
        InfoItem(Lucide.MapPinHouse, R.string.address, place.address.formatted_address)
        
        // Opening Hours logic (Lấy dữ liệu thật)
        val hoursText = if (place.opening_hours != null) {
             // Lấy lịch thứ 2 làm chuẩn hiển thị
             val today = place.opening_hours.mon 
             if (today != null) {
                 if (today.open == "00:00" && today.close == "23:59") "Open 24/7"
                 else "${today.open} - ${today.close}"
             } else "No Info"
        } else "No Info"
        
        InfoItem(Lucide.Clock3, R.string.opening_hours, hoursText)

        // Price logic (Lấy dữ liệu thật)
        val priceText = if (place.price_range != null) {
             val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
             "${format.format(place.price_range!!.min)} - ${format.format(place.price_range!!.max)} ${place.price_range!!.currency}"
        } else place.price_indicator // Fallback nếu null
        
        InfoItem(Lucide.Wallet, R.string.price_range, priceText)

        InfoItem(
            Lucide.Waypoints,
            R.string.distance,
            "~1.5km" // Placeholder for distance
        )
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