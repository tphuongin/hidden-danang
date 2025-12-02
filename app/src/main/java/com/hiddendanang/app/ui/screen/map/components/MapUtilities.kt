package com.hiddendanang.app.ui.screen.map.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.graphics.createBitmap
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds

/**
 * Decodes a Google polyline encoded string into a list of LatLng coordinates
 * @param encoded The encoded polyline string
 * @return List of LatLng points
 */
fun decodePolyline(encoded: String?): List<LatLng> {
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

/**
 * Converts a vector drawable to a bitmap
 * @param context Android context
 * @param id Drawable resource ID
 * @param width Bitmap width
 * @param height Bitmap height
 * @return Bitmap representation of the drawable
 */
@SuppressLint("UseCompatLoadingForDrawables")
fun vectorToBitmap(
    context: android.content.Context,
    id: Int,
    width: Int,
    height: Int
): Bitmap {
    val drawable = context.getDrawable(id)!!
    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

/**
 * Creates a simple circular marker bitmap
 * @param context Android context
 * @param color The color of the circle
 * @param size Size of the bitmap
 * @return A circular marker bitmap
 */
fun createSimpleMarkerBitmap(
    context: android.content.Context,
    color: Int,
    size: Int
): Bitmap {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Draw border (white outline for contrast)
    val borderPaint = android.graphics.Paint().apply {
        this.color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, (size / 2f) - 3, borderPaint)

    // Draw filled circle
    val paint = android.graphics.Paint().apply {
        this.color = color
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, (size / 2f) - 6, paint)

    return bitmap
}

/**
 * Builds a LatLngBounds from two locations with safe null handling
 * @param lat1 Latitude of first point
 * @param lng1 Longitude of first point
 * @param lat2 Latitude of second point
 * @param lng2 Longitude of second point
 * @return LatLngBounds or null if creation fails
 */
fun safeLatLngBounds(lat1: Double, lng1: Double, lat2: Double, lng2: Double): LatLngBounds? {
    return try {
        LatLngBounds.Builder()
            .include(LatLng(lat1, lng1))
            .include(LatLng(lat2, lng2))
            .build()
    } catch (e: Exception) {
        null
    }
}
