package com.hiddendanang.app.ui.screen.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.UserPen
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.utils.helpers.UserAvatar
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ProfileHeader(
    user: com.hiddendanang.app.data.model.User,
    favoriteCount: Int,
    reviewsCount: Int,
    visitedCount: Int,
    onEditClick: () -> Unit
) {
    val formattedDate = user.created_at?.let {timestamp ->
        val date = timestamp.toDate()
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
        formatter.format(date)
    } ?: "Không xác định"
    Card(
        shape = RoundedCornerShape(Dimens.CornerXLarge),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.ElevationHigh),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingMedium)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.PaddingLarge)
        ) {
            // Header Row (Avatar + Info + Edit)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(Dimens.AvatarLarge)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    UserAvatar(user.photo_url, user.display_name)
                }

                Spacer(Modifier.width(Dimens.SpaceLarge))

                // User Info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = Dimens.PaddingSmall)
                ) {
                    Text(
                        text = user.display_name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(R.string.joined, formattedDate),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = Dimens.PaddingNano)
                    )
                }

                // Edit Button
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(Dimens.IconXLarge)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Lucide.UserPen,
                        contentDescription = "Edit Profile",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.IconSmall)
                    )
                }
            }

            Spacer(Modifier.height(Dimens.SpaceLarge))

            // Divider line for clarity
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = Dimens.PaddingSmall),
                thickness = Dimens.StrokeMedium,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            Spacer(Modifier.height(Dimens.SpaceMedium))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem(count = favoriteCount, label = stringResource(R.string.favorites))
                ProfileStatItem(count = reviewsCount, label = stringResource(R.string.reviews))
                ProfileStatItem(count = visitedCount, label = stringResource(R.string.visited))
            }
        }
    }
}

@Composable
private fun ProfileStatItem(count: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
