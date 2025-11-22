package com.hiddendanang.app.ui.screen.map.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hiddendanang.app.R
import com.hiddendanang.app.data.model.goongmodel.DirectionResponse
import com.hiddendanang.app.data.model.goongmodel.Location
import com.hiddendanang.app.viewmodel.GoongViewModel
import kotlinx.coroutines.launch
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions
import org.maplibre.android.annotations.PolylineOptions
import androidx.core.graphics.createBitmap
import com.hiddendanang.app.utils.LocationService

@Composable
fun MapViewContent(
    destinationLocation: Location?,
    goongVM: GoongViewModel = viewModel()
) {
    val context = LocalContext.current
    val locationService = LocationService(context)
    val mapKey = stringResource(R.string.map_key)
    val coroutine = rememberCoroutineScope()
    val originLocation = Location(2.0, 3.0) // for test

    val direction by goongVM.directionsResponse.collectAsState()

    // Log direction state for debugging
    LaunchedEffect(direction) {
        android.util.Log.d("MapViewContent", "Direction state updated: $direction")
    }

    // Gọi Goong Direction API khi có đủ origin + destination
    LaunchedEffect(destinationLocation) {
        if (originLocation != null && destinationLocation != null) {
            goongVM.fetchDirections(
                "${originLocation.lat},${originLocation.lng}",
                "${destinationLocation.lat},${destinationLocation.lng}"
            )
        }
    }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
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
                    drawRoute(map, direction!!)
                } else {
                    // Log for debugging if direction is null
                    android.util.Log.e("MapViewContent", "Direction is null or not fetched yet.")
                }
            }
        }
    )
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

private fun addMarkers(
    map: MapLibreMap,
    mapView: MapView,
    origin: Location,
    destination: Location
) {
    map.getStyle { style ->
        val symbolManager = SymbolManager(mapView, map, style)
        symbolManager.iconAllowOverlap = true

        style.addImage(
            "origin-icon",
            vectorToBitmap(mapView.context, R.drawable.start, 80, 80)
        )
        style.addImage(
            "dest-icon",
            vectorToBitmap(mapView.context, R.drawable.location, 80, 80)
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
}

private fun decodePolyline(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
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
