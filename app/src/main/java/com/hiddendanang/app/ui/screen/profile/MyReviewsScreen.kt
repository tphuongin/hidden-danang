package com.hiddendanang.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.composables.icons.lucide.*
import com.hiddendanang.app.data.model.Review
import com.hiddendanang.app.navigation.Screen
import com.hiddendanang.app.ui.screen.detail.components.ReviewForm
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.viewmodel.MyReviewsViewModel
import com.hiddendanang.app.viewmodel.ReviewWithPlace
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReviewsScreen(
    navController: NavHostController,
    viewModel: MyReviewsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Dialog Edit State
    var showEditDialog by remember { mutableStateOf(false) }
    var reviewToEdit by remember { mutableStateOf<Review?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Reviews", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Lucide.ArrowLeft, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.reviews.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "You haven't written any reviews yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(Dimens.PaddingMedium),
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
                ) {
                    // SỬA: items nhận list ReviewWithPlace
                    items(uiState.reviews) { reviewWithPlace ->
                        MyReviewItem(
                            reviewWithPlace = reviewWithPlace,
                            onEdit = { 
                                reviewToEdit = reviewWithPlace.review
                                showEditDialog = true
                            },
                            onDelete = { viewModel.deleteReview(reviewWithPlace.review) },
                            onPlaceClick = {
                                // Điều hướng đến chi tiết Place khi bấm vào tên/ảnh quán
                                reviewWithPlace.place?.let { place ->
                                    navController.navigate(Screen.DetailPlace.createRoute(place.id))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Edit Dialog
    if (showEditDialog && reviewToEdit != null) {
        ReviewForm(
            initialRating = reviewToEdit!!.rating,
            initialComment = reviewToEdit!!.comment,
            onDismiss = { 
                showEditDialog = false 
                reviewToEdit = null
            },
            onSubmit = { rating, comment ->
                viewModel.updateReview(reviewToEdit!!, rating, comment)
                showEditDialog = false
                reviewToEdit = null
            }
        )
    }

    uiState.message?.let { message ->
        LaunchedEffect(message) {
            viewModel.messageShown()
        }
    }
}

@Composable
fun MyReviewItem(
    reviewWithPlace: ReviewWithPlace,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPlaceClick: () -> Unit
) {
    val review = reviewWithPlace.review
    val place = reviewWithPlace.place
    
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    val formattedDate = review.updated_at?.let { timestamp ->
        val date = timestamp.toDate()
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
        formatter.format(date)
    } ?: "Unknown Date"

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Review?") },
            text = { Text("Are you sure you want to delete this review?") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        onDelete()
                        showDeleteConfirm = false 
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    Card(
        shape = RoundedCornerShape(Dimens.CornerMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.PaddingMedium)
        ) {
            // --- Place Info Header (New) ---
            if (place != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlaceClick() }
                        .padding(bottom = Dimens.PaddingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Place Image
                    AsyncImage(
                        model = place.images.firstOrNull()?.url,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                    
                    // Place Name
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = place.address.district, // Hiển thị quận/huyện
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    
                    Icon(
                        imageVector = Lucide.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(bottom = Dimens.PaddingSmall))
            } else {
                // Fallback nếu không load được Place hoặc place_id rỗng
                if (review.place_id.isNotEmpty()) {
                     Text(
                        text = "Unknown Place (ID: ${review.place_id})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
                }
            }

            // --- Rating & Date ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${review.rating}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // --- Comment ---
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            
            // --- Actions ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Icon(Lucide.Pen, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Edit")
                }
                TextButton(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Lucide.Trash2, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
}