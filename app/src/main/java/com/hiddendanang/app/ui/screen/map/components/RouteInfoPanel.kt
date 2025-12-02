package com.hiddendanang.app.ui.screen.map.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.*
import com.hiddendanang.app.data.model.goongmodel.DirectionResponse
import com.hiddendanang.app.ui.theme.Dimens
import kotlin.math.roundToInt

data class RouteStep(
    val instruction: String,
    val distance: Double,
    val duration: Double
)

@Composable
fun RouteInfoPanel(
    direction: DirectionResponse?,
    originLat: Double,
    originLng: Double,
    destLat: Double,
    destLng: Double,
    destinationName: String = "Destination",
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var showSteps by remember { mutableStateOf(false) }

    val route = direction?.routes?.firstOrNull()
    val leg = route?.legs?.firstOrNull()

    val distance = (leg?.distance?.value ?: 0) / 1000.0 // meters to km
    val duration = (leg?.duration?.value ?: 0) / 60.0 // seconds to minutes
    val distanceStr = "%.1f".format(distance)
    val durationStr = "${duration.roundToInt()}"

    val steps = leg?.steps?.map { step ->
        RouteStep(
            instruction = step.htmlInstructions?.replace(Regex("<[^>]*>"), "") ?: "",
            distance = (step.distance?.value ?: 0) / 1000.0,
            duration = (step.duration?.value ?: 0) / 60.0
        )
    } ?: emptyList()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.PaddingMedium),
        shape = RoundedCornerShape(Dimens.CornerXLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.ElevationHigh)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingMedium)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Route Info",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(Lucide.X, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

            // Distance and Duration
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(Dimens.CornerLarge)
                    )
                    .padding(Dimens.PaddingMedium),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Distance
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Lucide.MapPin,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.IconMedium)
                    )
                    Text(
                        "$distanceStr km",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Distance",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Divider
                Divider(
                    modifier = Modifier
                        .height(50.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )

                // Duration
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Lucide.Clock,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.IconMedium)
                    )
                    Text(
                        "$durationStr min",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Duration",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

            // Route Destination
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(Dimens.CornerMedium)
                    )
                    .padding(Dimens.PaddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Lucide.Navigation,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.IconSmall)
                )
                Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Going to",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        destinationName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

            // Steps Toggle Button
            OutlinedButton(
                onClick = { showSteps = !showSteps },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    if (showSteps) Lucide.ChevronUp else Lucide.ChevronDown,
                    null,
                    modifier = Modifier.size(Dimens.IconSmall)
                )
                Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                Text("${steps.size} Steps")
            }

            // Steps List
            if (showSteps && steps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                ) {
                    items(steps) { step ->
                        RouteStepItem(step)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                // Navigate Button
                Button(
                    onClick = {
                        openNavigation(
                            context,
                            originLat,
                            originLng,
                            destLat,
                            destLng,
                            destinationName
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Lucide.Navigation2, null, modifier = Modifier.size(Dimens.IconSmall))
                    Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                    Text("Navigate")
                }

                // Share Button
                OutlinedButton(
                    onClick = {
                        shareRoute(
                            context,
                            originLat,
                            originLng,
                            destLat,
                            destLng,
                            distance,
                            duration,
                            destinationName
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(Lucide.Share2, null, modifier = Modifier.size(Dimens.IconSmall))
                    Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                    Text("Share")
                }
            }
        }
    }
}

@Composable
private fun RouteStepItem(step: RouteStep) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(Dimens.CornerMedium)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.PaddingSmall)
        ) {
            Text(
                step.instruction,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "%.2f km".format(step.distance),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "•",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${step.duration.roundToInt()} min",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun openNavigation(
    context: android.content.Context,
    originLat: Double,
    originLng: Double,
    destLat: Double,
    destLng: Double,
    destinationName: String
) {
    // Open Google Maps or Goong Maps for navigation
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(
            "https://www.google.com/maps/dir/$originLat,$originLng/$destLat,$destLng?travelmode=driving"
        )
        setPackage("com.google.android.apps.maps")
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to browser if Google Maps not installed
        val webIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://www.google.com/maps/dir/$originLat,$originLng/$destLat,$destLng?travelmode=driving"
            )
        }
        context.startActivity(webIntent)
    }
}

private fun shareRoute(
    context: android.content.Context,
    originLat: Double,
    originLng: Double,
    destLat: Double,
    destLng: Double,
    distance: Double,
    duration: Double,
    destinationName: String
) {
    val shareText = """
        Check out this route to $destinationName!
        
        Distance: %.1f km
        Duration: %d minutes
        
        View on map: https://www.google.com/maps/dir/$originLat,$originLng/$destLat,$destLng
    """.trimIndent().format(distance, duration.roundToInt())

    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    context.startActivity(Intent.createChooser(intent, "Share Route"))
}
