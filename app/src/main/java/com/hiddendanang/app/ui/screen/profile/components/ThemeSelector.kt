package com.hiddendanang.app.ui.screen.profile.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.utils.constants.AppThemeMode
import com.hiddendanang.app.utils.helpers.capitalizeFirstOnly

@Composable
fun ThemeSelector(
    currentTheme: AppThemeMode = AppThemeMode.LIGHT,
    onThemeChange: (AppThemeMode) -> Unit,
    themeModes: List<AppThemeMode> = listOf(
        AppThemeMode.LIGHT,
        AppThemeMode.DARK,
        AppThemeMode.SUNSET
    )
) {
    Card(
        shape = RoundedCornerShape(Dimens.CornerXLarge),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.ElevationMedium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingLarge)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.PaddingLarge)
        ) {
            Text(
                text = "Chế độ hiển thị",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium)
            ) {
                themeModes.forEach { mode ->
                    ThemeOption(
                        mode = mode,
                        isSelected = currentTheme == mode,
                        onClick = { onThemeChange(mode) },
                        modifier = Modifier.weight(1f) // Thêm weight ở đây
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeOption(
    mode: AppThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // Thêm modifier parameter
) {
    // Hiệu ứng động
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) Dimens.StrokeLarge else Dimens.StrokeMedium
    )

    // Màu sắc đại diện cho theme
    val themeColor = when (mode) {
        AppThemeMode.LIGHT -> Color(0xFFF5F5F5)
        AppThemeMode.DARK -> Color(0xFF2D2D2D)
        AppThemeMode.SUNSET -> Color(0xFFFFE4C9)
        AppThemeMode.SYSTEM -> {
            if (isSystemInDarkTheme()) Color(0xFF2D2D2D) else Color(0xFFF5F5F5)
        }
    }

    // Icon đại diện cho theme
    val themeIconColor = when (mode) {
        AppThemeMode.LIGHT -> Color(0xFF7ED6C1).copy(alpha = 0.8f)
        AppThemeMode.DARK -> Color.White.copy(alpha = 0.8f)
        AppThemeMode.SUNSET -> Color(0xFFFF6B35).copy(alpha = 0.8f)
        AppThemeMode.SYSTEM -> Color(0xFF2196F3).copy(alpha = 0.8f)
    }

    Column(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(Dimens.CornerLarge))
            .background(backgroundColor)
            .border(borderWidth, borderColor, RoundedCornerShape(Dimens.CornerLarge))
            .clickable { onClick() }
            .padding(vertical = Dimens.PaddingLarge, horizontal = Dimens.PaddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)
    ) {
        // Visual indicator của theme
        Box(
            modifier = Modifier
                .size(Dimens.ButtonSmall)
                .clip(RoundedCornerShape(Dimens.CornerMedium))
                .background(themeColor)
                .border(
                    Dimens.StrokeMedium,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    RoundedCornerShape(Dimens.CornerMedium)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Icon đại diện cho theme
            Box(
                modifier = Modifier
                    .size(Dimens.IconSmall)
                    .clip(RoundedCornerShape(Dimens.CornerMediumLarge))
                    .background(themeIconColor)
            )

            // Checkmark khi được chọn
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(Dimens.IconMedium)
                        .clip(RoundedCornerShape(Dimens.CornerLarge))
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = mode.name.capitalizeFirstOnly(),
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )

        // Indicator nhỏ phía dưới
        Box(
            modifier = Modifier
                .height(Dimens.StrokeXLarge)
                .fillMaxWidth(0.6f)
                .clip(RoundedCornerShape(Dimens.CornerSmall))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else Color.Transparent
                )
        )
    }
}