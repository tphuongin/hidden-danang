package com.hiddendanang.app.ui.components.place

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hiddendanang.app.ui.model.Place

@Composable
fun PlacesSection(
    places: List<Place>,
    title: String,
    onPlaceClick: ((placeId : String) -> Unit)? = null
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//            items(places) { place ->
//                PlaceCard(
//                    place = place,
//                    onClick = { onPlaceClick?.invoke(place.id) }
//                )
//            }
        }
    }
}
