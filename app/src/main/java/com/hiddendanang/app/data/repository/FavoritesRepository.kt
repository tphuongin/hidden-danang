package com.hiddendanang.app.data.repository

import androidx.room.util.copy
import com.google.firebase.auth.FirebaseAuth
import com.hiddendanang.app.data.model.Favorite
import com.hiddendanang.app.data.remote.FirestoreDataSource
import com.hiddendanang.app.navigation.Screen
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

    suspend fun removeFavorite(favoriteId: String): Result<Unit> {
        return try {
            val favoritesRef = dataSource.getFavoritesCollection(currentUserId)
            favoritesRef.document(favoriteId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // NOTE: Cần thêm hàm getFavorites để lấy danh sách yêu thích
}