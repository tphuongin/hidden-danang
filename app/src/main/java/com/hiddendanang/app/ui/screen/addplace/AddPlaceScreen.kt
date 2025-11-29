package com.hiddendanang.app.ui.screen.addplace

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.composables.icons.lucide.*
import com.hiddendanang.app.ui.screen.auth.ErrorDialog
import com.hiddendanang.app.ui.screen.auth.FullScreenLoading
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.viewmodel.AddPlaceUiState
import com.hiddendanang.app.viewmodel.AddPlaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(
    navController: NavHostController,
    viewModel: AddPlaceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            Toast.makeText(context, "Place submitted for review!", Toast.LENGTH_SHORT).show()
            viewModel.resetSuccess()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add New Place", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Lucide.ArrowLeft, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AddPlaceContent(
                uiState = uiState,
                viewModel = viewModel
            )

            if (uiState.isLoading) {
                FullScreenLoading()
            }
        }
    }

    uiState.error?.let {
        ErrorDialog(message = it, onDismiss = { viewModel.errorShown() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceContent(
    uiState: AddPlaceUiState,
    viewModel: AddPlaceViewModel
) {
    val scrollState = rememberScrollState()
    val categories = listOf(
        "category_food" to "Food & Drink",
        "category_coffee" to "Coffee",
        "category_sightseeing" to "Sightseeing",
        "category_chill" to "Chill Spot",
        "category_hidden" to "Hidden Gem"
    )
    var expanded by remember { mutableStateOf(false) }

    // Multiple Image Picker Launcher
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5), // Max 5 images
        onResult = { uris -> viewModel.onImagesSelected(uris) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(Dimens.PaddingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        // --- Images Section ---
        Text("Images (Min 3)", style = MaterialTheme.typography.titleMedium)
        
        if (uiState.selectedImages.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(120.dp)
            ) {
                items(uiState.selectedImages) { uri ->
                    Box {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        // Remove button
                        IconButton(
                            onClick = { viewModel.removeImage(uri) },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Lucide.X, "Remove", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                // Add more button
                item {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                multiplePhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Lucide.Plus, "Add more")
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(Dimens.CornerMedium))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        multiplePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Lucide.ImagePlus,
                        null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Tap to select images", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // --- Basic Info ---
        OutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChange,
            label = { Text("Place Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = categories.find { it.first == uiState.categoryId }?.second ?: "Select Category",
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { Icon(Lucide.ChevronDown, null) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { (id, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            viewModel.onCategoryChange(id)
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = uiState.address,
            onValueChange = viewModel::onAddressChange,
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.district,
            onValueChange = viewModel::onDistrictChange,
            label = { Text("District") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.description,
            onValueChange = viewModel::onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        HorizontalDivider()

        // --- Price Section ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = uiState.hasPriceInfo,
                onCheckedChange = viewModel::togglePriceInfo
            )
            Text("Has Price Info")
        }
        
        if (uiState.hasPriceInfo) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.minPrice,
                    onValueChange = viewModel::onMinPriceChange,
                    label = { Text("Min Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.maxPrice,
                    onValueChange = viewModel::onMaxPriceChange,
                    label = { Text("Max Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        HorizontalDivider()

        // --- Opening Hours Section ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = uiState.hasOpeningHours,
                onCheckedChange = viewModel::toggleOpeningHours
            )
            Text("Has Opening Hours")
        }

        if (uiState.hasOpeningHours) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.isOpen247,
                    onCheckedChange = viewModel::toggleOpen247
                )
                Text("Open 24/7")
            }

            if (!uiState.isOpen247) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.openTime,
                        onValueChange = viewModel::onOpenTimeChange,
                        label = { Text("Open (HH:mm)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = uiState.closeTime,
                        onValueChange = viewModel::onCloseTimeChange,
                        label = { Text("Close (HH:mm)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // Placeholder for Map Picker
        Button(
            onClick = { /* TODO: Open Map Picker */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.filledTonalButtonColors()
        ) {
            Icon(Lucide.MapPin, null)
            Spacer(Modifier.width(8.dp))
            Text("Pick Location on Map (Coming Soon)")
        }

        Button(
            onClick = viewModel::submitPlace,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.PaddingLarge),
            enabled = !uiState.isLoading
        ) {
            Text("Submit Location")
        }
    }
}