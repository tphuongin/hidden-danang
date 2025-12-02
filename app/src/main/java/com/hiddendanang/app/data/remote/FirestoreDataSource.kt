package com.hiddendanang.app.data.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class FirestoreDataSource {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val placesRef = firestore.collection("places")
    private val usersRef = firestore.collection("users")

    suspend fun fetchPopularPlacesRaw(): QuerySnapshot {
        return placesRef
            .whereEqualTo("status", "active")
            .orderBy("popularity_score", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .await()
    }

    fun getPlaceDocumentReference(placeId: String): DocumentReference {
        return placesRef.document(placeId)
    }

    fun getUserDocumentReference(uid: String): DocumentReference {
        return usersRef.document(uid)
    }
    fun getPlacesCollection(): CollectionReference {
        return firestore.collection("places") // ✅ tên collection trong Firestore
    }

    fun getFavoritesCollection(uid: String) =
        usersRef.document(uid).collection("favorites")

    fun getReviewsCollection(placeId: String): CollectionReference {
        return placesRef.document(placeId).collection("reviews")
    }

    /**
     * Lấy tham chiếu Document Review DUY NHẤT của User: /places/{placeId}/reviews/{userId}
     * Dùng để READ trạng thái Review cũ và WRITE (submit/update).
     */
    fun getUserReviewDocumentReference(placeId: String, userId: String): DocumentReference {
        return getReviewsCollection(placeId).document(userId)
    }
}