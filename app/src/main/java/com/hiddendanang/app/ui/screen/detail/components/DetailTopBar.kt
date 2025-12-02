package com.hiddendanang.app.ui.screen.detail.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.Share2
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.theme.Dimens
import android.content.Intent

@Composable
fun DetailTopBar(
    navController: NavHostController,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    placeName: String = "Hidden Đà Nẵng",
    modifier: Modifier = Modifier
) {
    val heartScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = ""
    )
    val context = LocalContext.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
// Back Button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .size(Dimens.ButtonMedium)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Lucide.ChevronLeft,
                contentDescription = "Back",
                modifier = Modifier.size(Dimens.IconMedium)
            )
        }
// Share Button
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            IconButton(
                onClick = {
                    val shareText = context.getString(R.string.share_place, placeName)
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)))
                },
                modifier = Modifier
                    .size(Dimens.ButtonMedium)
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Lucide.Share2,
                    contentDescription = stringResource(R.string.share)
                )
            }
// Favorite Button
            Box(
                modifier = Modifier
                    .background(
                        color = if (isFavorite) Color.Red
                        else MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
                    .size(Dimens.ButtonMedium)
                    .clickable { onToggleFavorite() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.Heart,
                    contentDescription = stringResource(R.string.favorite),
                    tint = if (isFavorite) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.graphicsLayer(
                        scaleX = heartScale,
                        scaleY = heartScale
                    )
                )
            }
        }
    }
}