package com.hiddendanang.app.data.model.goongmodel

data class DirectionResponse(
    val geocodedWaypoints: List<GeocodedWaypoint>?,
    val routes: List<Route>?,
    val executionTime: String?,
    val status: String?
)

data class GeocodedWaypoint(
    val geocoderStatus: String?,
    val placeId: String?
)

data class Route(
    val bounds: Bounds?,
    val legs: List<Leg>?,
    val overviewPolyline: Polyline?,
    val summary: String?,
    val warnings: List<String>?,
    val waypointOrder: List<Int>?
)

data class Bounds(
    val northeast: Location?,
    val southwest: Location?
)

data class Leg(
    val distance: Distance?,
    val duration: Duration?,
    val endAddress: String?,
    val endLocation: Location?,
    val startAddress: String?,
    val startLocation: Location?,
    val steps: List<Step>?
)

data class Step(
    val distance: Distance?,
    val duration: Duration?,
    val endLocation: Location?,
    val htmlInstructions: String?,
    val maneuver: String?,
    val polyline: Polyline?,
    val startLocation: Location?,
    val travelMode: String?
)

data class Distance(
    val text: String?,
    val value: Int?
)

data class Duration(
    val text: String?,
    val value: Int?
)

data class Location(
    val lat: Double?,
    val lng: Double?
)

data class Polyline(
    val points: String?
)
