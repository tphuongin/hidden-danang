package com.hiddendanang.app.ui.screen.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.theme.Dimens

@Composable
fun SearchBar() {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = { Text(stringResource(R.string.search), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
        leadingIcon = {
            Icon(
                imageVector = Lucide.Search,
                contentDescription = stringResource(R.string.search_des),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        },
        shape = RoundedCornerShape(Dimens.CornerXLarge),
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.SearchBarHeight),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}
