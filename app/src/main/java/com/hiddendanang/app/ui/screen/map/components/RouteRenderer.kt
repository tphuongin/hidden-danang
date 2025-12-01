package com.hiddendanang.app.ui.screen.map.components

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import com.hiddendanang.app.data.model.goongmodel.DirectionResponse

/**
 * Renders the route on the map based on DirectionResponse
 * @param map The MapLibreMap instance
 * @param direction The DirectionResponse containing route data
 */
fun renderMap(map: MapLibreMap, direction: DirectionResponse) {
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
                decodePolyline(step.polyline?.points).map { point ->
                    LatLng(point.latitude, point.longitude)
                }
            })
        }
        map.addPolyline(polylineOptions)
    } else {
        Log.w("RouteRenderer", "No route found in direction response.")
    }
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
    val bounds = safeLatLngBounds(originLat, originLng, destLat, destLng)
    if (bounds != null) {
        map.animateCamera(org.maplibre.android.camera.CameraUpdateFactory.newLatLngBounds(bounds, 20))
    } else {
        Log.w("RouteRenderer", "Failed to create bounds for camera movement")
    }
}
