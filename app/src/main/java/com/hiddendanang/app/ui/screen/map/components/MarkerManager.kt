package com.hiddendanang.app.ui.screen.map.components

import android.util.Log
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.model.goongmodel.Location

fun addMarkers(
    map: MapLibreMap,
    mapView: MapView,
    origin: Location,
    destination: Location,
    originDrawableId: Int,
    destDrawableId: Int
) {
    map.getStyle { style ->
        val symbolManager = SymbolManager(mapView, map, style)
        symbolManager.iconAllowOverlap = true

        // Add custom images for markers
        try {
            style.addImage(
                "origin-icon",
                vectorToBitmap(mapView.context, originDrawableId, 80, 80)
            )
            style.addImage(
                "dest-icon",
                vectorToBitmap(mapView.context, destDrawableId, 140, 140)
            )

            val originSymbol = SymbolOptions()
                .withLatLng(LatLng(origin.lat!!, origin.lng!!))
                .withIconImage("origin-icon")
                .withIconSize(1.8f)

            val destSymbol = SymbolOptions()
                .withLatLng(LatLng(destination.lat!!, destination.lng!!))
                .withIconImage("dest-icon")
                .withIconSize(2.2f)

            symbolManager.create(originSymbol)
            symbolManager.create(destSymbol)

        } catch (e: Exception) {
            Log.e("MarkerManager", "❌ Error adding markers: ${e.message}")
        }
    }
}


fun addNearbyPlacesMarkers(
    map: MapLibreMap,
    mapView: MapView,
    nearbyPlaces: List<Place>,
    currentLocation: Location,
    currentLocationDrawableId: Int,
    nearbyPlaceDrawableId: Int,
    onMarkerClick: (Place) -> Unit = {}
) {
    map.getStyle { style ->
        val symbolManager = SymbolManager(mapView, map, style)
        symbolManager.iconAllowOverlap = true

        // Add custom images for markers
        try {
            try {
                style.addImage(
                    "current-location-icon",
                    vectorToBitmap(mapView.context, currentLocationDrawableId, 80, 80)
                )
            } catch (e: Exception) {
                style.addImage(
                    "current-location-icon",
                    createSimpleMarkerBitmap(mapView.context, android.graphics.Color.RED, 100)
                )
            }

            try {
                style.addImage(
                    "nearby-place-icon",
                    vectorToBitmap(mapView.context, nearbyPlaceDrawableId, 80, 80)
                )
            } catch (e: Exception) {
                style.addImage(
                    "nearby-place-icon",
                    createSimpleMarkerBitmap(mapView.context, android.graphics.Color.BLUE, 100)
                )
            }
        } catch (e: Exception) {
            Log.e("MarkerManager", "❌ Error adding images to style: ${e.message}")
        }

        // Add marker for current location
        try {
            val currentSymbol = SymbolOptions()
                .withLatLng(LatLng(currentLocation.lat!!, currentLocation.lng!!))
                .withIconImage("current-location-icon")
                .withIconSize(1.5f)
            symbolManager.create(currentSymbol)
        } catch (e: Exception) {
            Log.e("MarkerManager", "❌ Error adding current location marker: ${e.message}")
        }

        // Add markers for nearby places with pulse animation and click listener
        nearbyPlaces.forEachIndexed { index, place ->
            try {
                place.coordinates.let { location ->
                    val nearbySymbol = SymbolOptions()
                        .withLatLng(LatLng(location.latitude, location.longitude))
                        .withIconImage("nearby-place-icon")
                        .withIconSize(1.8f)
                    val marker = symbolManager.create(nearbySymbol)

                    // Add pulse animation with delay for each marker
                    val delay = index * 200L
                    addPulseAnimation(symbolManager, marker, delay)

                    // Add click listener for this marker
                    symbolManager.addClickListener { clickedMarker ->
                        if (clickedMarker == marker) {
                            onMarkerClick(place)
                            true
                        } else {
                            false
                        }
                    }

                }
            } catch (e: Exception) {
                Log.e("MarkerManager", "❌ Error adding marker for ${place.name}: ${e.message}")
            }
        }
    }
}
