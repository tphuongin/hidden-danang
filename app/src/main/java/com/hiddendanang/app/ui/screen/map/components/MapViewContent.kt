package com.hiddendanang.app.ui.screen.map.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hiddendanang.app.R
import com.hiddendanang.app.data.model.goongmodel.DirectionResponse
import com.hiddendanang.app.data.model.goongmodel.Location
import com.hiddendanang.app.viewmodel.GoongViewModel
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.geometry.LatLngBounds
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import com.hiddendanang.app.data.model.Place
import org.maplibre.android.plugins.annotation.SymbolManager
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.hiddendanang.app.ui.components.place.PlaceCard
import com.hiddendanang.app.ui.components.Loading
import com.hiddendanang.app.ui.model.DataViewModel
import com.hiddendanang.app.ui.theme.Dimens

private var tooltipMapShownOnce = false

@Composable
fun MapViewContent(
    destinationLocation: Location?,
    goongVM: GoongViewModel = viewModel(),
    nearbyPlaces: List<Place> = emptyList(),
    navController: NavHostController? = null,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val context = LocalContext.current
    val mapKey = stringResource(R.string.map_key)
    val dataViewModel: DataViewModel = viewModel()

    // State for selected place popup
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    
    // State for map loading
    var isMapLoading by remember { mutableStateOf(true) }

    // Parse the currentLocation string into a Location object
    val currentLocationStr = goongVM.currentLocation.collectAsState().value
    val originLocation = currentLocationStr?.split(",")?.let {
        if (it.size == 2) {
            val lat = it[0].toDoubleOrNull()
            val lng = it[1].toDoubleOrNull()
            if (lat != null && lng != null) {
                Location(lat, lng)
            } else {
                null
            }
        } else {
            null
        }
    }

    // Default location for fallback (Đại học Bách Khoa - Đại học Đà Nẵng)
    val defaultDaNangLocation = Location(16.0736606, 108.149869)

    val direction by goongVM.directionsResponse.collectAsState()

    // Fetch current location when component is first created
    LaunchedEffect(Unit) {
        goongVM.fetchCurrentLocation()
    }

    // Fetch nearby places when location changes
    LaunchedEffect(originLocation) {
        if (destinationLocation == null && originLocation != null && originLocation.lat != null && originLocation.lng != null) {
            goongVM.fetchNearbyPlaces(originLocation.lat, originLocation.lng)
        }
    }

    // Get nearby places from ViewModel
    val mapNearbyPlaces by goongVM.nearbyPlaces.collectAsState()

    // Gọi Goong Direction API khi có đủ origin + destination
    val mapView = remember { MapView(context) }

    LaunchedEffect(destinationLocation, originLocation, nearbyPlaces, mapNearbyPlaces) {
        if (destinationLocation == null) {
            mapView.getMapAsync { map ->
                // Display nearby places markers from ViewModel (auto-fetched)
                val placesToShow = mapNearbyPlaces.ifEmpty { nearbyPlaces }

                if (originLocation != null && placesToShow.isNotEmpty()) {
                    addNearbyPlacesMarkersInternal(map, mapView, placesToShow, originLocation) { clickedPlace ->
                        selectedPlace = clickedPlace
                    }
                    // Hide loading when nearby places are displayed
                    isMapLoading = false
                    
                    // Build bounds including origin and all nearby places
                    val boundsBuilder = LatLngBounds.Builder()
                    boundsBuilder.include(LatLng(originLocation.lat!!, originLocation.lng!!))
                    placesToShow.forEach { place ->
                        place.coordinates.let {
                            boundsBuilder.include(LatLng(it.latitude, it.longitude))
                        }
                    }
                    
                    try {
                        val bounds = boundsBuilder.build()
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
                    } catch (e: Exception) {
                        // Failed to set bounds, use default Da Nang view
                        val daNangBounds = LatLngBounds.Builder()
                            .include(LatLng(16.047079, 108.206230))
                            .include(LatLng(16.153, 108.151))
                            .include(LatLng(15.975, 108.250))
                            .build()
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(daNangBounds, 150))
                    }
                } 
            }
        } else if (destinationLocation.lat != null && destinationLocation.lng != null) {
            if (originLocation != null) {
                goongVM.fetchDirections(
                    "${originLocation.lat},${originLocation.lng}",
                    "${destinationLocation.lat},${destinationLocation.lng}"
                )
                // Hide loading for direction case
                isMapLoading = false
            }
        }
    }




    // Observe direction state and trigger rendering when updated
    LaunchedEffect(direction) {
        if (direction != null) {
            mapView.getMapAsync { map ->
                renderMapInternal(context, map, direction!!)
            }
        }
    }

    var showTooltip by remember { mutableStateOf(false) }
    if (!tooltipMapShownOnce && direction == null && destinationLocation == null && !isMapLoading) {
        showTooltip = true
        tooltipMapShownOnce = true
    }
    androidx.compose.runtime.LaunchedEffect(showTooltip) {
        if (showTooltip) {
            kotlinx.coroutines.delay(11500L)
            showTooltip = false
        }
    }

    AndroidView(
        factory = { ctx ->
            mapView.apply {
                getMapAsync { map ->
                    // Set initial camera position to Da Nang before loading style
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(16.0736606, 108.149869), 12.0))
                    
                    map.setStyle(
                        "https://tiles.goong.io/assets/goong_map_web.json?api_key=$mapKey"
                    ) { style ->
                        if (style.isFullyLoaded) {
                            // Map style loaded

                            // Only add markers if location has been fetched from GPS and destination is selected
                            if (originLocation != null && destinationLocation != null) {
                                addMarkers(
                                    map = map,
                                    mapView = this,
                                    origin = originLocation,
                                    destination = destinationLocation,
                                    originDrawableId = R.drawable.start,
                                    destDrawableId = R.drawable.location
                                )

                                // Add direction marker if direction is available
                                if (direction != null && direction!!.routes?.isNotEmpty() == true) {
                                    val firstStep = direction!!.routes?.firstOrNull()?.legs?.firstOrNull()?.steps?.firstOrNull()
                                    if (firstStep != null) {
                                        map.getStyle { style ->
                                            try {
                                                style.addImage(
                                                    "direction-icon",
                                                    vectorToBitmap(context, R.drawable.location, 80, 80)
                                                )
                                                val directionSymbol = org.maplibre.android.plugins.annotation.SymbolOptions()
                                                    .withLatLng(org.maplibre.android.geometry.LatLng(
                                                        firstStep.startLocation?.lat!!, firstStep.startLocation.lng!!))
                                                    .withIconImage("direction-icon")
                                                    .withIconSize(1.8f)
                                                val symbolManager = org.maplibre.android.plugins.annotation.SymbolManager(this, map, style)
                                                symbolManager.iconAllowOverlap = true
                                                symbolManager.create(directionSymbol)
                                            } catch (e: Exception) {
                                                // Error adding direction marker
                                            }
                                        }
                                    }
                                }

                                moveCameraToBounds(
                                    map,
                                    originLocation.lat!!,
                                    originLocation.lng!!,
                                    destinationLocation.lat!!,
                                    destinationLocation.lng!!
                                )
                            }

                            // Trigger map rendering with direction
                            if (direction != null) {
                                renderMapInternal(context, map, direction!!)
                            }
                        } else {
                            // Failed to load style, using default
                        }
                    }
                }
            }
        },
        update = { mapView ->
            mapView.getMapAsync { map ->
                if (direction != null) {
                    // Direction response received
                    renderMapInternal(context, map, direction!!)
                } else {
                    // Skipping map rendering
                }
            }
        },
        modifier = modifier
    )

    // Popup card dialog for selected place
    selectedPlace?.let { place ->
        Dialog(
            onDismissRequest = { selectedPlace = null },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { selectedPlace = null },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clickable(enabled = false) { },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(Dimens.CornerXLarge),
                    shadowElevation = Dimens.ElevationXLarge,
                    tonalElevation = Dimens.ElevationMedium
                ) {
                        val isFavoriteState = dataViewModel.isFavorite(place.id).collectAsState()
                        val isFavorite = isFavoriteState.value
                        
                        PlaceCard(
                            place = place,
                            isFavorite = isFavorite,
                            onClick = { placeId ->
                                try {
                                    if (navController != null) {
                                        navController.navigate("detail-place/$placeId")
                                        selectedPlace = null
                                    }
                                } catch (e: Exception) {
                                    // Error navigating
                                }
                            },
                            onFavoriteToggle = {
                                try {
                                    // Toggle favorite
                                    dataViewModel.toggleFavorite(place.id)
                                } catch (e: Exception) {
                                    // Error toggling favorite
                                }
                            }
                        )

                }
            }
        }
    }

    Box(modifier = modifier) {
        // Show RouteInfoPanel when direction is available
        if (direction != null && originLocation != null && destinationLocation != null) {
            RouteInfoPanel(
                direction = direction,
                originLat = originLocation.lat!!,
                originLng = originLocation.lng!!,
                destLat = destinationLocation.lat!!,
                destLng = destinationLocation.lng!!,
                destinationName = selectedPlace?.name ?: "Destination",
                onClose = {
                    // Reset direction and selected place
                    selectedPlace = null
                    goongVM.clearDirections()
                }
            )
        }

        if (showTooltip && destinationLocation == null && !isMapLoading) {
            com.hiddendanang.app.ui.components.TooltipHint(
                text = stringResource(com.hiddendanang.app.R.string.hint_map_no_direction),
                visible = true,
                onDismiss = { showTooltip = false },
                autoDismissDelayMs = 7000L,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = com.hiddendanang.app.ui.theme.Dimens.PaddingXLarge)
            )
        }
        
        // Show loading animation when map is loading
        if (isMapLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            ) {
                Loading(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

// Add nearby places markers
private fun addNearbyPlacesMarkersInternal(
    map: MapLibreMap,
    mapView: MapView,
    nearbyPlaces: List<Place>,
    currentLocation: Location,
    onMarkerClick: (Place) -> Unit = {}
) {
    addNearbyPlacesMarkers(
        map = map,
        mapView = mapView,
        nearbyPlaces = nearbyPlaces,
        currentLocation = currentLocation,
        currentLocationDrawableId = R.drawable.start,
        nearbyPlaceDrawableId = R.drawable.location,
        onMarkerClick = onMarkerClick
    )
}



// Update renderMap function to accept MapLibreMap instance
private fun renderMapInternal(context: Context, map: MapLibreMap, direction: DirectionResponse) {
    // Delegate to RouteRenderer.kt
    renderMap(context, map, direction)
}
