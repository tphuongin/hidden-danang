package com.hiddendanang.app.ui.screen.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.components.InteractiveDirectionsMap
import com.hiddendanang.app.ui.model.DataViewModel
import com.hiddendanang.app.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveMapScreen(
    navController: NavHostController,
    placeId: String
) {
    val dataViewModel: DataViewModel = viewModel()
    val place by dataViewModel.selectedPlace.collectAsState()

    LaunchedEffect(placeId) {
        dataViewModel.getPlaceById(placeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = place?.name?.let { stringResource(id = R.string.map_directions_to, it) } ?: stringResource(id = R.string.map_directions),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(start = Dimens.PaddingTiny)
                            .size(Dimens.IconLarge)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Lucide.ChevronLeft,
                            contentDescription = stringResource(id = R.string.back_button_description),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),

                modifier = Modifier.fillMaxWidth()
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (place != null) {
                    InteractiveDirectionsMap(
                        place = place!!,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(Dimens.ProgressBarSize),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = Dimens.DividerThick
                    )
                }
            }
        }
    )
}
