package com.hiddendanang.app.data.repository

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.remote.FirestoreDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class LocationRepository {

    private val remoteDataSource: FirestoreDataSource = FirestoreDataSource()


    fun getPlacesStreamByCategory(categoryId: String): Flow<List<Place>> {
        // [1] Lấy tham chiếu Collection từ DataSource
        val collectionRef = remoteDataSource.getPlacesCollection()

        // [2] Thiết lập Query cơ bản: Lọc theo trạng thái và sắp xếp theo độ phổ biến
        val baseQuery = collectionRef
            .whereEqualTo("status", "active")
            .orderBy("popularity_score", Query.Direction.DESCENDING)

        // [3] Áp dụng bộ lọc category_id nếu cần
        val query = if (categoryId.isNotEmpty() && categoryId != "all") {
            // Áp dụng bộ lọc cho danh mục cụ thể
            baseQuery.whereEqualTo("category_id", categoryId)
        } else {
            // Trường hợp "Tất cả" hoặc ID rỗng: Trả về Query cơ bản (tất cả địa điểm hoạt động)
            baseQuery
        }

        // [4] Chuyển đổi Query thành Flow<List<Place>> (Real-time)
        // Đây là điểm quan trọng nhất: dataObjects<Place>() chuyển đổi Firestore Query thành Flow<List<Place>>
        return query.dataObjects<Place>()
    }
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

    suspend fun getAllPlaces(): Result<List<Place>> {
        return try {
            // 1. Lấy tham chiếu đến collection "places"
            val snapshot = remoteDataSource.getPlacesCollection().get().await()

            // 2. Chuyển đổi tất cả documents thành List<Place>
            //    Sử dụng .copy(id = doc.id) để đảm bảo ID của document được gán vào object,
            //    rất quan trọng cho việc điều hướng.
            val places = snapshot.documents.mapNotNull { doc ->
                doc.toObject<Place>()?.copy(id = doc.id)
            }

            // 3. Trả về kết quả thành công
            Result.success(places)
        } catch (e: Exception) {
            // 4. Trả về lỗi nếu có
            Result.failure(e)
        }
    }
    suspend fun getNearbyPlaces(currentPlaceGeohash: String, limit: Long = 10): Result<List<Place>> {
        if (currentPlaceGeohash.isEmpty()) {
            return Result.success(emptyList())
        }

        return try {
            // First, try to get places with exact or nearby geohash prefix (5 chars)
            val geohashPrefix = currentPlaceGeohash.take(5)

            val query = remoteDataSource.getPlacesCollection()
                .whereGreaterThanOrEqualTo("coordinates.geohash", geohashPrefix)
                .whereLessThanOrEqualTo("coordinates.geohash", geohashPrefix + "\uf8ff")
                .limit(limit)

            val snapshot = query.get().await()
            
            if (snapshot.documents.isNotEmpty()) {
                val places = snapshot.documents.mapNotNull { doc ->
                    doc.toObject<Place>()?.copy(id = doc.id)
                }
                return Result.success(places)
            }
            
            // If no results with geohash, fetch ALL places and filter by distance (3km radius)
            
            val allPlacesSnapshot = remoteDataSource.getPlacesCollection().get().await()
            
            // Get center point from first place's geohash (aproximate), or use hardcoded Da Nang center
            var centerLat = 16.0736606
            var centerLng = 108.149869
            
            val places = allPlacesSnapshot.documents.mapNotNull { doc ->
                val place = doc.toObject<Place>()?.copy(id = doc.id)
                place
            }
            
            // Filter places within 3km radius using Haversine formula
            val radiusKm = 3.0
            val nearbyPlaces = places.filter { place ->
                if (place.coordinates == null) {
                    false
                } else {
                    val distance = haversineDistance(
                        centerLat, centerLng,
                        place.coordinates!!.latitude, place.coordinates!!.longitude
                    )
                    distance <= radiusKm
                }
            }.take(limit.toInt())
            
            Result.success(nearbyPlaces)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.asin(Math.sqrt(a))
        return R * c
    }
    suspend fun addPlace(place: Place): Result<Unit> {
        return try {
            val collection = remoteDataSource.getPlacesCollection()
            val docRef = if (place.id.isNotEmpty()) collection.document(place.id) else collection.document()
            val placeToSave = place.copy(id = docRef.id)
            docRef.set(placeToSave).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}