package com.hiddendanang.app.ui.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.components.PlacesSection
import com.hiddendanang.app.ui.model.Place
import com.hiddendanang.app.ui.model.PlaceViewModel
import com.hiddendanang.app.ui.model.Review
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailScreen(
    navController: NavController,
    place: Place,
    onToggleFavortie: () -> Unit,
    onCLose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentImageIndex by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val viewmodel: PlaceViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.onBackground)
    ) {
        DetailImageCarousel(
            images = place.images,
            onCLose = onCLose,
            onToggleFavortie = onToggleFavortie,
            onShare = { /* TODO: Share */ }
        )


        DetailContent(
            place = place,
            viewModel = viewmodel
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailImageCarousel(
    images: List<String>,
    onCLose: () -> Unit,
    onToggleFavortie: () -> Unit,
    onShare: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { images.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(384.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            GlideImage(
                model = images[page],
                contentDescription = "Image ${page + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Top Bar
        DetailTopBar(
            onBack = onCLose,
            onShare = onShare,
            onToggleFavortie = onToggleFavortie
        )

        // Image Index (Bottom Right)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${pagerState.currentPage + 1}/${images.size}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Dot Indicator (Bottom Center)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(images.size) { index ->
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(if (index == pagerState.currentPage) 20.dp else 4.dp)
                        .background(
                            color = if (index == pagerState.currentPage)
                                MaterialTheme.colorScheme.primary
                            else
                                Color.White.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(2.dp)
                        )
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun DetailTopBar(
    onBack: () -> Unit,
    onShare: () -> Unit,
    onToggleFavortie: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(top = 16.dp), // Thêm padding trên để cân bằng với thanh trạng thái
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                modifier = Modifier.size(24.dp)
            )

        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        )
        {
            IconButton(
                onClick = onShare,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share"
                )
            }
            IconButton(
                onClick = onToggleFavortie,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite"
                )
            }
        }
    }
}

@Composable
fun DetailContent(
    place: Place,
    viewModel: PlaceViewModel // Pass ViewModel or necessary data
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        PlaceTitleAndRating(place = place)
        PlaceInfoSection(place = place)
        PlaceDescription(description = place.description)
        MapAndDirectionSection(place = place)
        ReviewsSection(reviews = place.reviews)
        PlacesSection(
            places = viewModel.topPlace, "Khám phá gần đây"
        )
    }
}

@Composable
fun PlaceTitleAndRating(place: Place) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = place.name,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rating and Review Count
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_star),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                )
                Text(
                    text = "${place.rating}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = ". 300 đánh giá ", // Hardcoded for now, should come from data
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                )
            }
            // Category Tag
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = place.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun PlaceInfoSection(place: Place) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoItem(painterResource(R.drawable.ic_locationon), "Địa chỉ", place.address)
        InfoItem(painterResource(R.drawable.ic_clockxml), "Giờ mở cửa", place.hours)
        InfoItem(painterResource(R.drawable.ic_wallet), "Giá trung bình", place.priceRange)
        InfoItem(
            painterResource(R.drawable.ic_distance),
            "Khoảng cách",
            "Cách bạn ${place.distance}"
        )
    }
}

@Composable
fun PlaceDescription(description: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Giới thiệu",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MapAndDirectionSection(place: Place) {
    // Button map
    Button(
        onClick = { /* TODO: Implement navigation to map */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .padding(end = 8.dp)
        )
        Text(
            "Xem đường đi",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
    // Card map
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(224.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Map image placeholder
            GlideImage(
                model = place.images, // Giả sử Place có thuộc tính mapImage
                contentDescription = "Map",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Location Pin Icon
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun ReviewsSection(reviews: List<Review>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Đánh giá",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Show up to 3 reviews
            repeat(minOf(3, reviews.size)) { index ->
                ReviewCard(reviews[index])
            }
        }
        // Button view all comments
        Button(
            onClick = { /* TODO: Implement view all comments */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.outlinedButtonColors()
        ) {
            Text(
                "Xem tất cả đánh giá",
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
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                "Viết đánh giá của bạn",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun InfoItem(icon: Painter, label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.user.take(1),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = review.user,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = review.timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFFBBF24).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${review.rating}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}