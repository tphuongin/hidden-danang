package com.hiddendanang.app.ui.components

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.hiddendanang.app.ui.model.Place
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

private const val MAP_STYLE_URL = "https://demotiles.maplibre.org/style.json"
private const val DEFAULT_MAP_ZOOM = 15.0
private const val DEFAULT_MAP_TILT = 45.0
private const val CAMERA_ANIMATION_DURATION_MS = 1500

@Composable
fun InteractiveDirectionsMap(
    place: Place,
    modifier: Modifier = Modifier,
    onMapReady: (MapLibreMap) -> Unit = {}
) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
        }
    }

    DisposableEffect(Unit) {
        mapView.onStart()
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    ) { view ->
        view.getMapAsync { map ->
            setupInteractiveMap(map, place)
            onMapReady(map)
        }
    }
}

private fun setupInteractiveMap(map: MapLibreMap, place: Place) {
    map.setStyle(Style.Builder().fromUri(MAP_STYLE_URL)) { style ->
        val destination = LatLng(place.latitude, place.longitude)
        val cameraPosition = CameraPosition.Builder()
            .target(destination)
            .zoom(DEFAULT_MAP_ZOOM)
            .tilt(DEFAULT_MAP_TILT)
            .build()

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), CAMERA_ANIMATION_DURATION_MS)

        map.addMarker(
            MarkerOptions()
                .position(destination)
                .title(place.name)
                .snippet(place.address)
        )

        with(map.uiSettings) {
            isZoomGesturesEnabled = true
            isScrollGesturesEnabled = true
            isRotateGesturesEnabled = true
            isTiltGesturesEnabled = true
            isCompassEnabled = true
            isLogoEnabled = true
        }

        // Xử lý sự kiện click marker
//        map.setOnMarkerClickListener { marker ->
//            marker.showInfoWindow(map, mapView)
//            true
//        }
    }
}
