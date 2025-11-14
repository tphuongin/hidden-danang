package com.hiddendanang.app.ui.screen.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.composables.icons.lucide.Lucide
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.composables.icons.lucide.ArrowLeft
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.ui.screen.home.navToDetailScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController
) {
    val viewModel: SearchViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            // Thanh tìm kiếm THẬT nằm ở đây
            TopAppBar(
                title = {
                    // Đây là TextField tìm kiếm
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        placeholder = { Text(stringResource(R.string.search)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Lucide.ArrowLeft, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            items(
                items = uiState.searchResults,
                key = { place -> place.id }
            ) { place ->
                // Hiển thị từng mục đề xuất
                ListItem(
                    headlineContent = { Text(place.name) },
                    supportingContent = { Text(place.address.formatted_address, maxLines = 1) },
                    modifier = Modifier.clickable {
                        // Bấm vào đề xuất -> đi đến chi tiết
                        navToDetailScreen(navController, place.id)
                    }
                )
            }
        }
    }
}