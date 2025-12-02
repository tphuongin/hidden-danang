package com.hiddendanang.app.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.theme.Dimens

@Composable
fun SearchBar(
    onClick: () -> Unit, // Thay đổi quan trọng: Biến nó thành nút bấm
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.SearchBarHeight)
            .clip(RoundedCornerShape(Dimens.CornerXLarge))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(Dimens.CornerXLarge)
            )
            .clickable { onClick() }, // Hành động khi bấm
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(Dimens.PaddingMedium))

        Icon(
            imageVector = Lucide.Search,
            contentDescription = stringResource(R.string.search_des),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.width(Dimens.PaddingSmall))

        Text(
            text = stringResource(R.string.search),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}