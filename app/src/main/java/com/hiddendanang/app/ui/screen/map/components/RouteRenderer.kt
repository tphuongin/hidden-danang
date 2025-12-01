package com.hiddendanang.app.ui.screen.map.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.hiddendanang.app.R
import com.hiddendanang.app.data.model.goongmodel.DirectionResponse
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap

/**
 * Renders the route on the map based on DirectionResponse
 * @param map The MapLibreMap instance
 * @param direction The DirectionResponse containing route data
 */
fun renderMap(context: Context, map: MapLibreMap, direction: DirectionResponse) {
    val route = direction.routes?.firstOrNull()
    if (route != null) {
        val bounds = route.bounds
        val steps = route.legs?.flatMap { leg -> leg.steps ?: emptyList() } ?: emptyList()

        // Update camera bounds
        val cameraBounds = LatLngBounds.Builder().apply {
            bounds?.northeast?.let { include(LatLng(it.lat!!, it.lng!!)) }
            bounds?.southwest?.let { include(LatLng(it.lat!!, it.lng!!)) }
            // Include start and end locations explicitly
            route.legs?.firstOrNull()?.startLocation?.let { include(LatLng(it.lat!!, it.lng!!)) }
            route.legs?.lastOrNull()?.endLocation?.let { include(LatLng(it.lat!!, it.lng!!)) }

            // Include intermediate waypoints
            route.legs?.flatMap { leg -> leg.steps ?: emptyList() }?.forEach { step ->
                step.endLocation?.let { include(LatLng(it.lat!!, it.lng!!)) }
            }
        }.let {
            try {
                it.build()
            } catch (e: Exception) {
                Log.e("RouteRenderer", "Failed to create LatLngBounds: ${e.message}")
                null
            }
        }

        // Move camera to bounds
        if (cameraBounds != null) {
            map.moveCamera(org.maplibre.android.camera.CameraUpdateFactory.newLatLngBounds(cameraBounds, 150))
        } else {
            Log.w("RouteRenderer", "Skipping camera movement due to invalid bounds.")
        }

        // Draw polyline for the route
        val polylineOptions = PolylineOptions().apply {
            color(Color.Blue.toArgb())
            width(10f)
            addAll(steps.flatMap { step ->
                decodePolylineLocal(step.polyline?.points).map { point ->
                    LatLng(point.latitude, point.longitude)
                }
            })
        }
        map.addPolyline(polylineOptions)

        // Add markers for start and end points
        val iconFactory = IconFactory.getInstance(context)

        // Create scaled bitmaps for markers and then create icons from them
        val startBitmap = createScaledMarkerIcon(context, R.drawable.start, 100, 100)
        val startIcon = iconFactory.fromBitmap(startBitmap)

        val endBitmap = createScaledMarkerIcon(context, R.drawable.location, 100, 100)
        val endIcon = iconFactory.fromBitmap(endBitmap)


        route.legs?.firstOrNull()?.let { startLeg ->
            startLeg.startLocation?.let { start ->
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(start.lat!!, start.lng!!))
                        .title(startLeg.startAddress ?: "Điểm bắt đầu")
                        .icon(startIcon)
                )
            }
        }
        route.legs?.lastOrNull()?.let { endLeg ->
            endLeg.endLocation?.let { end ->
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(end.lat!!, end.lng!!))
                        .title(endLeg.endAddress ?: "Điểm đến")
                        .icon(endIcon)
                )
            }
        }
    } else {
        Log.w("RouteRenderer", "No route found in direction response.")
    }
}

/**
 * Creates a scaled bitmap from a drawable resource.
 */
private fun createScaledMarkerIcon(context: Context, drawableId: Int, width: Int, height: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableId)
        ?: throw IllegalArgumentException("Drawable not found")
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}


/**
 * Moves camera to fit both origin and destination
 * @param map The MapLibreMap instance
 * @param originLat Origin latitude
 * @param originLng Origin longitude
 * @param destLat Destination latitude
 * @param destLng Destination longitude
 */
fun moveCameraToBounds(
    map: MapLibreMap,
    originLat: Double,
    originLng: Double,
    destLat: Double,
    destLng: Double
) {
    val bounds = createLatLngBoundsSafely(originLat, originLng, destLat, destLng)
    if (bounds != null) {
        map.animateCamera(org.maplibre.android.camera.CameraUpdateFactory.newLatLngBounds(bounds, 20))
    } else {
        Log.w("RouteRenderer", "Failed to create bounds for camera movement")
    }
}

fun decodePolylineLocal(encoded: String?): List<LatLng> {
    if (encoded.isNullOrEmpty()) {
        return emptyList()
    }
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
        poly.add(p)
    }
    return poly
}

fun createLatLngBoundsSafely(originLat: Double, originLng: Double, destLat: Double, destLng: Double): LatLngBounds? {
    return try {
        LatLngBounds.Builder()
            .include(LatLng(originLat, originLng))
            .include(LatLng(destLat, destLng))
            .build()
    } catch (e: Exception) {
        Log.e("RouteRenderer", "Failed to create LatLngBounds: ${e.message}")
        null
    }
}
