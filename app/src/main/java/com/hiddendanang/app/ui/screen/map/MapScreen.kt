package com.hiddendanang.app.ui.screen.map

import androidx.compose.runtime.Composable
import com.hiddendanang.app.data.model.goongmodel.Location
import com.hiddendanang.app.ui.screen.map.components.MapViewContent
import com.hiddendanang.app.utils.LocationPermission

@Composable
fun MapScreen(
    origin: Location?,
    destination: Location?
) {
    LocationPermission(
        onGranted = {
            MapViewContent(origin, destination)
        }
    )
}
