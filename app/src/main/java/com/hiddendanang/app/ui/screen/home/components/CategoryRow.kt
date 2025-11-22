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

// BỔ SUNG: Thêm trường ID thực tế cho Firestore Query
data class Category(val id: String, val name: String, val icon: ImageVector)

@Composable
fun CategoryRow(
    // 1. THAM SỐ MỚI: Nhận ID danh mục hiện tại (State)
    currentCategory: String,
    // 2. THAM SỐ MỚI: Callback khi người dùng chọn danh mục (Action)
    onCategorySelected: (categoryId: String) -> Unit
) {
    // Dữ liệu danh mục và ID thực tế của chúng
    val categories = remember {
        listOf(
            Category("all", "Tất cả", Lucide.List),
            Category("category_food", "Ăn uống", Lucide.Utensils),
            Category("category_coffee", "Cà phê", Lucide.Coffee),
            Category("category_sightseeing", "Du lịch", Lucide.Map),
            Category("category_chill", "Góc chill", Lucide.Sofa),
            Category("category_hidden", "Hidden Gems", Lucide.Gem)
        )
    }
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
                // KIỂM TRA TRẠNG THÁI: Dùng ID để so sánh, không dùng index
                isSelected = currentCategory == category.id,
                onClick = {
                    // GỌI CALLBACK: Truyền ID danh mục thực tế lên ViewModel
                    onCategorySelected(category.id)
                }
            )
        }
    }
}