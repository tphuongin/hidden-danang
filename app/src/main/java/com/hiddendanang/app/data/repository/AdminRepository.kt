package com.hiddendanang.app.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.model.User
import kotlinx.coroutines.tasks.await

class AdminRepository {
    private val db = Firebase.firestore

    // --- USERS ---
    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val snapshot = db.collection("users").get().await()
            val users = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserRole(userId: String, newRole: String): Result<Unit> {
        return try {
            db.collection("users").document(userId).update("role", newRole).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            db.collection("users").document(userId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- PLACES ---
    suspend fun getAllPlaces(filterStatus: String? = null): Result<List<Place>> {
        return try {
            val query = if (filterStatus != null) {
                db.collection("places").whereEqualTo("status", filterStatus)
            } else {
                db.collection("places")
            }
            val snapshot = query.get().await()
            val places = snapshot.documents.mapNotNull { it.toObject(Place::class.java)?.copy(id = it.id) }
            Result.success(places)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePlaceStatus(placeId: String, newStatus: String): Result<Unit> {
        return try {
            db.collection("places").document(placeId).update("status", newStatus).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePlace(placeId: String): Result<Unit> {
        return try {
            db.collection("places").document(placeId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}