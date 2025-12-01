package com.hiddendanang.app.ui.screen.map.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.hiddendanang.app.data.model.Place
import org.maplibre.android.plugins.annotation.SymbolManager
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.hiddendanang.app.ui.components.place.PlaceCard
import com.hiddendanang.app.ui.model.DataViewModel
import com.hiddendanang.app.ui.theme.Dimens

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
            goongVM.fetchNearbyPlaces(originLocation.lat!!, originLocation.lng!!)
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
                android.util.Log.d("MapViewContent", "Displaying ${placesToShow.size} nearby places.")

                if (originLocation != null && placesToShow.isNotEmpty()) {
                    android.util.Log.d("MapViewContent", "Displaying ${placesToShow.size} nearby places.")
                    addNearbyPlacesMarkersInternal(map, mapView, placesToShow, originLocation) { clickedPlace ->
                        selectedPlace = clickedPlace
                    }
                    
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
                        android.util.Log.e("MapViewContent", "Failed to set bounds: ${e.message}")
                        val daNangBounds = LatLngBounds.Builder()
                            .include(LatLng(16.047079, 108.206230))
                            .include(LatLng(16.153, 108.151))
                            .include(LatLng(15.975, 108.250))
                            .build()
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(daNangBounds, 150))
                    }
                } else if (originLocation == null) {
                    // If location not fetched yet, display Da Nang default bounds
                    val daNangBounds = LatLngBounds.Builder()
                        .include(LatLng(16.047079, 108.206230))
                        .include(LatLng(16.153, 108.151))
                        .include(LatLng(15.975, 108.250))
                        .build()
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(daNangBounds, 150))
                }
            }
        } else if (destinationLocation.lat != null && destinationLocation.lng != null) {
            if (originLocation != null) {
                goongVM.fetchDirections(
                    "${originLocation.lat},${originLocation.lng}",
                    "${destinationLocation.lat},${destinationLocation.lng}"
                )
            }
        } else {
            android.util.Log.w("MapViewContent", "Origin or destination location is invalid.")
        }
    }

    // Log the current location for debugging
    LaunchedEffect(originLocation) {
        android.util.Log.d("MapViewContent", "Current origin location: $originLocation")
    }


    // Observe direction state and trigger rendering when updated
    LaunchedEffect(direction) {
        if (direction != null) {
            android.util.Log.d("MapViewContent", "Rendering map with updated direction state.")
            mapView.getMapAsync { map ->
                renderMapInternal(map, direction!!)
            }
        } else {
            android.util.Log.w("MapViewContent", "Skipping map rendering as direction is null.")
        }
    }

    AndroidView(
        factory = { ctx ->
            mapView.apply {
                getMapAsync { map ->
                    map.setStyle(
                        "https://tiles.goong.io/assets/goong_map_web.json?api_key=$mapKey"
                    ) { style ->
                        if (style.isFullyLoaded) {
                            Log.d("MapViewContent", "Goong map style loaded successfully.")

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
                                renderMapInternal(map, direction!!)
                            }
                        } else {
                            Log.e("MapViewContent", "Failed to load Goong map style. Default style applied.")
                        }
                    }
                }
            }
        },
        update = { mapView ->
            mapView.getMapAsync { map ->
                if (direction != null) {
                    Log.d("MapViewContent", "DirectionResponse: $direction")
                    renderMapInternal(map, direction!!)
                } else {
                    Log.w("MapViewContent", "Skipping map rendering as direction is null.")
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
                                    Log.d("MapViewContent", "Popup card clicked for place: $placeId")
                                    if (navController != null) {
                                        Log.d("MapViewContent", "Navigating to detail-place/$placeId")
                                        navController.navigate("detail-place/$placeId")
                                        selectedPlace = null
                                    } else {
                                        Log.e("MapViewContent", "NavController is null, cannot navigate")
                                    }
                                } catch (e: Exception) {
                                    Log.e("MapViewContent", "Error navigating to detail: ${e.message}", e)
                                }
                            },
                            onFavoriteToggle = {
                                try {
                                    Log.d("MapViewContent", "Toggle favorite for place: ${place.id}")
                                    dataViewModel.toggleFavorite(place.id)
                                } catch (e: Exception) {
                                    Log.e("MapViewContent", "Error toggling favorite: ${e.message}", e)
                                }
                            }
                        )

                }
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
private fun renderMapInternal(map: MapLibreMap, direction: DirectionResponse) {
    // Delegate to RouteRenderer.kt
    renderMap(map, direction)
}
