package com.hiddendanang.app.ui.screen.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hiddendanang.app.data.model.goongmodel.Location
import com.hiddendanang.app.ui.screen.map.components.MapViewContent
import com.hiddendanang.app.utils.LocationPermission

@Composable
fun MapScreen(
    destination: Location?,
    navController: NavHostController? = null
) {
    LocationPermission(
        onGranted = {
            MapViewContent(
                destinationLocation = destination,
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
    )
}
