package com.hiddendanang.app.data.repository

import androidx.room.util.copy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.dataObjects
import com.hiddendanang.app.data.model.Favorite
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.remote.FirestoreDataSource
import com.hiddendanang.app.navigation.Screen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FavoritesRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val dataSource: FirestoreDataSource = FirestoreDataSource()
) {
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    suspend fun addFavorite(favoriteData: Favorite): Result<Unit> {
        return try {
            val favoritesRef = dataSource.getFavoritesCollection(currentUserId)

            favoritesRef.document().set(
                favoriteData.copy(user_id = currentUserId)
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFavoriteByPlaceId(placeId: String): Result<Unit> {
        return try {
            val favoritesRef = dataSource.getFavoritesCollection(currentUserId)
            // 1. Tìm document Favorite nào có location_id == placeId
            val query = favoritesRef.whereEqualTo("location_id", placeId).limit(1).get().await()

            if (query.isEmpty) {
                // Không tìm thấy, có thể đã xóa, coi như thành công
                return Result.success(Unit)
            }

            // 2. Xóa document đó
            val favoriteDocId = query.documents.first().id
            favoritesRef.document(favoriteDocId).delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getFavoritePlacesStream(): Flow<List<Place>> {
        return try {
            // 1. Lắng nghe danh sách Favorite (nhẹ)
            dataSource.getFavoritesCollection(currentUserId)
                .dataObjects<Favorite>()
                .flatMapLatest { favorites ->
                    // 2. Từ danh sách Favorite, trích xuất các location_id
                    val locationIds = favorites.map { it.location_id }.distinct()

                    if (locationIds.isEmpty()) {
                        // Nếu không yêu thích gì, trả về list rỗng
                        flowOf(emptyList<Place>())
                    } else {
                        // 3. Lấy các Place_Details có Document ID nằm trong danh sách
                        // (Lưu ý: Firestore 'whereIn' giới hạn 30 item)
                        dataSource.getPlacesCollection() // Giả sử hàm này trả về collection "places"
                            .whereIn(FieldPath.documentId(), locationIds)
                            .dataObjects<Place>() // Tự động map sang List<Place>
                    }
                }
        } catch (e: Exception) {
            throw IllegalStateException("User not logged in", e)
        }
    }

    fun getFavoriteIdsStream(): Flow<Set<String>> {
        return try {
            dataSource.getFavoritesCollection(currentUserId)
                .dataObjects<Favorite>()
                .map { favoritesList ->
                    // Chuyển List<Favorite> thành Set<String> (chỉ ID)
                    favoritesList.map { it.location_id }.toSet()
                }
        } catch (e: Exception) {
            throw IllegalStateException("User not logged in", e)
        }
    }

}