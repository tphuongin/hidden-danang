package com.hiddendanang.app.ui.screen.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.* // Đảm bảo import đầy đủ
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ChevronLeft
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.screen.detail.components.ReviewCard
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllReviewsScreen(
    placeId: String, // THÊM tham số này
    onBack: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    // THÊM: Gọi load data khi screen được khởi tạo
    LaunchedEffect(placeId) {
        viewModel.listenToDataChanges(placeId)
    }

    val reviewList by viewModel.allReviews.collectAsState(initial = emptyList())
    val isLoading by viewModel.isReviewsLoading.collectAsState(initial = true)
    val allReview = stringResource(R.string.all_review)
    val noReview = stringResource(R.string.no_review)


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text( "$allReview(${reviewList.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Lucide.ChevronLeft,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                reviewList.isEmpty() -> {
                    Text(
                        text = noReview,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Dimens.SpaceLarge),
                        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium)
                    ) {
                        items(reviewList) { review ->
                            ReviewCard(review = review)
                        }
                    }
                }
            }
        }
    }
}