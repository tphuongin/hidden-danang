package com.hiddendanang.app.ui.screen.map.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions
import org.maplibre.android.annotations.PolylineOptions
import androidx.core.graphics.createBitmap
import com.hiddendanang.app.utils.LocationService
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.exceptions.InvalidLatLngBoundsException
import org.maplibre.android.geometry.LatLngBounds

@Composable
fun MapViewContent(
    destinationLocation: Location?,
    goongVM: GoongViewModel = viewModel()
) {
    val context = LocalContext.current
    val locationService = LocationService(context)
    val mapKey = stringResource(R.string.map_key)
    val coroutine = rememberCoroutineScope()

    // Set default origin location to Đại học Bách Khoa - Đại học Đà Nẵng
    val originLocation = Location(16.0736606, 108.149869)
    // Parse the currentLocation string into a Location object
//    val originLocation = goongVM.currentLocation.collectAsState().value?.split(",")?.let {
//        if (it.size == 2) {
//            val lat = it[0].toDoubleOrNull()
//            val lng = it[1].toDoubleOrNull()
//            if (lat != null && lng != null) {
//                Location(lat, lng)
//            } else {
//                null
//            }
//        } else {
//            null
//        }
//    } ?: defaultOriginLocation // Use default location if current location is null

    val direction by goongVM.directionsResponse.collectAsState()

    // Log direction state for debugging
    LaunchedEffect(direction) {
        android.util.Log.d("MapViewContent", "Direction state updated: $direction")
    }

    // Gọi Goong Direction API khi có đủ origin + destination
    val mapView = remember { MapView(context) } // Ensure mapView is defined in the correct scope

    LaunchedEffect(destinationLocation, originLocation) {
        goongVM.fetchCurrentLocation()
        if (destinationLocation == null) {
            android.util.Log.d("MapViewContent", "No destination provided. Displaying Da Nang city bounds.")
            mapView.getMapAsync { map ->
                val daNangBounds = LatLngBounds.Builder()
                    .include(LatLng(16.047079, 108.206230)) // Approximate center of Da Nang
                    .include(LatLng(16.153, 108.151)) // Northern boundary
                    .include(LatLng(15.975, 108.250)) // Southern boundary
                    .build()
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(daNangBounds, 150)) // Adjusted padding for better view
            }
        } else if (originLocation != null &&
            destinationLocation.lat != null && destinationLocation.lng != null) {
            goongVM.fetchDirections(
                "${originLocation.lat},${originLocation.lng}",
                "${destinationLocation.lat},${destinationLocation.lng}"
            )
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
                renderMap(map, direction!!)
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
                            android.util.Log.d("MapViewContent", "Goong map style loaded successfully.")

                            if (originLocation != null && destinationLocation != null) {
                                addMarkers(
                                    map = map,
                                    mapView = this,
                                    origin = originLocation,
                                    destination = destinationLocation
                                )

                                moveCameraToBounds(map, originLocation, destinationLocation)
                            }

                            // Trigger map rendering with direction
                            if (direction != null) {
                                renderMap(map, direction!!)
                            }
                        } else {
                            android.util.Log.e("MapViewContent", "Failed to load Goong map style. Default style applied.")
                        }
                    }
                }
            }
        },
        update = { mapView ->
            mapView.getMapAsync { map ->
                if (direction != null) {
                    android.util.Log.d("MapViewContent", "DirectionResponse: $direction")
                    renderMap(map, direction!!)
                } else {
                    android.util.Log.w("MapViewContent", "Skipping map rendering as direction is null.")
                }
            }
        }
    )
}

// Update renderMap function to accept MapLibreMap instance
private fun renderMap(map: MapLibreMap, direction: DirectionResponse) {
    val route = direction.routes?.firstOrNull()
    if (route != null) {
        val bounds = route.bounds
        // Ensure null safety and explicitly define the type for flatMap
        val steps = route.legs?.flatMap { leg -> leg.steps ?: emptyList() } ?: emptyList()

        // Update camera bounds
        // Ensure bounds are valid before building LatLngBounds
        // Ensure all relevant points are included in LatLngBounds
        val cameraBounds = LatLngBounds.Builder().apply {
            bounds?.northeast?.let { include(LatLng(it.lat!!, it.lng!!)) }
            bounds?.southwest?.let { include(LatLng(it.lat!!, it.lng!!)) }
            // Include start and end locations explicitly
            route.legs?.firstOrNull()?.startLocation?.let { include(LatLng(it.lat!!, it.lng!!)) }
            route.legs?.lastOrNull()?.endLocation?.let { include(LatLng(it.lat!!, it.lng!!)) }
            // Include intermediate waypoints

            // Explicitly specify the type for flatMap and ensure null safety
            route.legs?.flatMap { leg -> leg.steps ?: emptyList() }?.forEach { step ->
                step.endLocation?.let { include(LatLng(it.lat!!, it.lng!!)) }
            }
        }.let {
            try {
                it.build()
            } catch (e: InvalidLatLngBoundsException) {
                android.util.Log.e("MapViewContent", "Failed to create LatLngBounds: ${e.message}")
                null // Return null if bounds are invalid
            }
        }

        // Reduce padding for a closer camera view
        if (cameraBounds != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(cameraBounds, 150)) // Increased padding to 150
        } else {
            android.util.Log.w("MapViewContent", "Skipping camera movement due to invalid bounds.")
        }

        // Draw polyline for the route
        // Convert Color to Int for PolylineOptions
        val polylineOptions = PolylineOptions().apply {
            color(Color.Blue.toArgb()) // Convert Color to ARGB Int
            width(10f)
            addAll(steps.flatMap { decodePolyline(it.polyline?.points).map { point -> LatLng(point.latitude, point.longitude) } })
        }
        map.addPolyline(polylineOptions)

        // Place markers for start and end locations
        val startLocation = route.legs?.firstOrNull()?.startLocation
        val endLocation = route.legs?.lastOrNull()?.endLocation
        startLocation?.let {
            map.addMarker(MarkerOptions().position(LatLng(it.lat!!, it.lng!!)).title("Start"))
        }
        endLocation?.let {
            map.addMarker(MarkerOptions().position(LatLng(it.lat!!, it.lng!!)).title("End"))
        }
    } else {
        Log.w("MapViewContent", "No route found in direction response.")
    }
}

private fun moveCameraToBounds(
    map: MapLibreMap,
    origin: Location,
    destination: Location
) {
    val bounds = org.maplibre.android.geometry.LatLngBounds.Builder()
        .include(LatLng(origin.lat!!, origin.lng!!))
        .include(LatLng(destination.lat!!, destination.lng!!))
        .build()

    // Reduced padding for a closer view
    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20))
}

private fun drawRoute(map: MapLibreMap, direction: DirectionResponse) {
    val route = direction.routes?.firstOrNull()
    if (route == null) {
        android.util.Log.e("MapViewContent", "No routes found in DirectionResponse.")
        return
    }

    val polyPoints = decodePolyline(route.overviewPolyline?.points ?: "")
    android.util.Log.d("MapViewContent", "Decoded polyline points: $polyPoints")

    if (polyPoints.isEmpty()) {
        android.util.Log.e("MapViewContent", "Polyline decoding returned no points.")
        return
    }

    val polyline = PolylineOptions()
        .addAll(polyPoints)
        .width(8f) // Increased width for better visibility
        .color(0xFF00AEEF.toInt())

    map.addPolyline(polyline)
    android.util.Log.d("MapViewContent", "Polyline added to map successfully.")
}

// Update markers to use custom images
private fun addMarkers(
    map: MapLibreMap,
    mapView: MapView,
    origin: Location,
    destination: Location
) {
    map.getStyle { style ->
        val symbolManager = SymbolManager(mapView, map, style)
        symbolManager.iconAllowOverlap = true

        // Add custom images for markers
        style.addImage(
            "origin-icon",
            vectorToBitmap(mapView.context, R.drawable.start, 80, 80) // Replace with your custom image
        )
        style.addImage(
            "dest-icon",
            vectorToBitmap(mapView.context, R.drawable.location, 80, 80) // Replace with your custom image
        )

        val o = SymbolOptions()
            .withLatLng(LatLng(origin.lat!!, origin.lng!!))
            .withIconImage("origin-icon")

        val d = SymbolOptions()
            .withLatLng(LatLng(destination.lat!!, destination.lng!!))
            .withIconImage("dest-icon")

        symbolManager.create(o)
        symbolManager.create(d)
    }

    // Log the current map style and destination coordinates
    map.getStyle { style ->
        android.util.Log.d("MapViewContent", "Current map style: ${style.uri}")
    }

}

private fun decodePolyline(encoded: String?): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded?.length
    var lat = 0
    var lng = 0

    while (index < len!!) {
        var result = 0
        var shift = 0
        var b: Int
        do {
                b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
        lat += dlat

        result = 0
        shift = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
        lng += dlng

        poly.add(LatLng(lat / 1E5, lng / 1E5))
    }
    return poly
}

@SuppressLint("UseCompatLoadingForDrawables")
private fun vectorToBitmap(context: android.content.Context, id: Int, width: Int, height: Int): Bitmap {
    val drawable = context.getDrawable(id)!!
    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}
