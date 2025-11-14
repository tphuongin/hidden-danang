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
    suspend fun getNearbyPlaces(currentPlaceGeohash: String, limit: Long = 10): Result<List<Place>> {
        if (currentPlaceGeohash.isEmpty()) {
            return Result.success(emptyList()) // Trả về rỗng nếu không có geohash
        }

        return try {
            // Lấy tiền tố geohash (ví dụ: "w7gx6y" -> "w7gx6")
            val geohashPrefix = currentPlaceGeohash.dropLast(1)

            // Tạo query "bắt đầu bằng"
            // Tìm tất cả doc có geohash từ "w7gx6" đến "w7gx6~"
            val query = remoteDataSource.getPlacesCollection()
                .whereGreaterThanOrEqualTo("coordinates.geohash", geohashPrefix)
                .whereLessThanOrEqualTo("coordinates.geohash", geohashPrefix + "\uf8ff") // \uf8ff là ký tự Unicode cao nhất
                .limit(limit)

            val snapshot = query.get().await()
            val places = snapshot.documents.mapNotNull { doc ->
                doc.toObject<Place>()?.copy(id = doc.id)
            }

            Result.success(places)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}