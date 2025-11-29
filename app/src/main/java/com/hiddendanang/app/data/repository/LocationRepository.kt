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