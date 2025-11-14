package com.hiddendanang.app.ui.screen.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import com.hiddendanang.app.ui.model.DataViewModel
import com.hiddendanang.app.data.model.Review
import com.hiddendanang.app.data.model.User
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.utils.helpers.UserAvatar
import com.hiddendanang.app.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReviewCard(review: Review, modifier: Modifier = Modifier) {
    val viewModel: AuthViewModel = viewModel()
    LaunchedEffect(review.user_id) {
        viewModel.getUserById(review.user_id)
    }

    val formattedDate = review.created_at?.let {timestamp ->
        val date = timestamp.toDate()
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
        formatter.format(date)
    } ?: "Không xác định"

    val userState = viewModel.user.collectAsState().value
    if (userState == null) {
        return
    }
    val user = userState   // Lúc này user chắc chắn là non-null
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.PaddingXLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.SpaceLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(Dimens.WeightMedium)
                ) {
                    Box(
                        modifier = Modifier
                            .size(Dimens.ButtonMedium)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        UserAvatar(user.photo_url, user.display_name, size = 32)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.PaddingTiny)) {
                        Text(
                            text = user.display_name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingNano),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.Star,
                        contentDescription = "Rating",
                        tint = Color.Yellow,
                        modifier = Modifier.size(Dimens.IconSmall)
                    )
                    Text(
                        text = "${review.rating}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}