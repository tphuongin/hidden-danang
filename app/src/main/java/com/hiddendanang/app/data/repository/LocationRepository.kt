package com.hiddendanang.app.data.repository

import com.google.firebase.firestore.toObject
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.remote.FirestoreDataSource
import kotlinx.coroutines.tasks.await

class LocationRepository {

    private val remoteDataSource: FirestoreDataSource = FirestoreDataSource()

    suspend fun getPopularPlaces(): Result<List<Place>> {
        return try {
            val snapshot = remoteDataSource.fetchPopularPlacesRaw()
            val places = snapshot.documents.mapNotNull { it.toObject<Place>() }
            Result.success(places)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getPlaceDetail(placeId: String): Result<Place> {
        return try {
            val document = remoteDataSource.getPlaceDocumentReference(placeId).get().await()
            val place = document.toObject<Place>()

            if (place != null) Result.success(place)
            else Result.failure(Exception("Place not found for ID: $placeId"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}