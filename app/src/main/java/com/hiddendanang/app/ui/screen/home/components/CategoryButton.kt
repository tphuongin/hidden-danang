package com.hiddendanang.app.ui.screen.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.hiddendanang.app.ui.theme.Dimens

@Composable
fun CategoryButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, label = ""
    )
    val borderColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), label = ""
    )
    val textColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, label = ""
    )
    val elevation by animateDpAsState(if (isSelected) Dimens.ElevationMedium else Dimens.ElevationNone, label = "")

    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation),
        border = BorderStroke(Dimens.StrokeMedium, borderColor),
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        contentPadding = PaddingValues(horizontal = Dimens.PaddingLarge, vertical = Dimens.PaddingXTiny),
        modifier = Modifier.height(Dimens.ButtonSmall)
    ) {
        Row {
            Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(Dimens.IconTiny))
            Spacer(modifier = Modifier.width(Dimens.SpaceTiny))
            Text(text, color = textColor, fontWeight = FontWeight.Medium)
        }
    }
}
