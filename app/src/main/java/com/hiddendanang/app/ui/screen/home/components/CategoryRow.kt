package com.hiddendanang.app.ui.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.*
import com.hiddendanang.app.ui.theme.Dimens

data class Category(val name: String, val icon: ImageVector)

@Composable
fun CategoryRow() {
    val categories = listOf(
        Category("Tất cả", Lucide.List),
        Category("Ăn uống", Lucide.Utensils),
        Category("Cà phê", Lucide.Coffee),
        Category("Du lịch", Lucide.Map),
        Category("Góc chill", Lucide.Sofa),
        Category("Hidden Gems", Lucide.Gem)
    )
    var selectedIndex by remember { mutableIntStateOf(0) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.PaddingSmall),
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
        contentPadding = PaddingValues(horizontal = Dimens.PaddingLarge)
    ) {
        itemsIndexed(categories) { index, category ->
            CategoryButton(
                text = category.name,
                icon = category.icon,
                isSelected = selectedIndex == index,
                onClick = { selectedIndex = index }
            )
        }
    }
}
