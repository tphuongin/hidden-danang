package com.hiddendanang.app.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import com.composables.icons.lucide.Locate
import com.composables.icons.lucide.Lucide
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.hiddendanang.app.ui.theme.Dimens
import kotlinx.coroutines.tasks.await
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationService(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @androidx.annotation.RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    suspend fun getCurrentLocation(): String? {
        if (!hasLocationPermission()) {
            return null
        }
        return try {
            // Use getCurrentLocation API instead of lastLocation for real-time location
            val location: Location? = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(callback: OnTokenCanceledListener) = this
                    override fun isCancellationRequested() = false
                }
            ).await()
            
            location?.let {
                "${it.latitude},${it.longitude}"
            } ?: run {
                // Fallback to lastLocation if getCurrentLocation returns null
                val lastLocation: Location? = fusedLocationClient.lastLocation.await()
                lastLocation?.let {
                    "${it.latitude},${it.longitude}"
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val R = 6371.0 // Bán kính Trái Đất (km)
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return R * c
    }
}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermission(
    onGranted: @Composable () -> Unit
) {
    val permissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Auto request popup when entering the screen
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    when {
        permissionState.status.isGranted -> {
            onGranted()
        }

        permissionState.status.shouldShowRationale -> {
            PermissionRationaleUI(
                title = stringResource(id = com.hiddendanang.app.R.string.location_permission_title),
                message = stringResource(id = com.hiddendanang.app.R.string.location_permission_message),
                buttonText = stringResource(id = com.hiddendanang.app.R.string.location_permission_button),
                onButtonClick = {
                    permissionState.launchPermissionRequest()
                }
            )
        }

        else -> {
            PermissionRationaleUI(
                title = stringResource(id = com.hiddendanang.app.R.string.location_permission_required_title),
                message = stringResource(id = com.hiddendanang.app.R.string.location_permission_required_message),
                buttonText = stringResource(id = com.hiddendanang.app.R.string.location_permission_grant_button),
                onButtonClick = {
                    permissionState.launchPermissionRequest()
                }
            )
        }
    }
}
@Composable
fun PermissionRationaleUI(
    title: String,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.PaddingXLarge),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(Dimens.PaddingLarge))
                .padding(Dimens.PaddingXLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Lucide.Locate,
                contentDescription = null,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(Dimens.ButtonLarge)
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceTiny))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

            Button(
                onClick = onButtonClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.CornerMediumLarge)
            ) {
                Text(text = buttonText)
            }
        }
    }
}
