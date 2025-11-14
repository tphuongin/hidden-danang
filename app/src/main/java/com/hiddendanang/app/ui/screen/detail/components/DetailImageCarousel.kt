package com.hiddendanang.app.ui.screen.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.hiddendanang.app.data.model.ImageDetail
import com.hiddendanang.app.ui.theme.Dimens
import kotlinx.coroutines.launch

@Composable
fun DetailImageCarousel(
    navController: NavHostController,
    images: List<ImageDetail>,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { images.size })
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(images) {
        println("Images size: ${images.size}")
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.ContainerMedium)
            .clip(RoundedCornerShape(bottomStart = Dimens.PaddingXLarge, bottomEnd = Dimens.PaddingXLarge))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = images[page].url,
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
            navController = navController,
            isFavorite = isFavorite,
            onToggleFavorite = onToggleFavorite,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(Dimens.PaddingLarge)
        )

        // Image Index (Bottom Right)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Dimens.PaddingXLarge)
                .background(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall)
        ) {
            Text(
                text = "${pagerState.currentPage + 1}/${images.size}",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }

        // Dot Indicator (Bottom Center)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Dimens.PaddingXLarge),
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
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