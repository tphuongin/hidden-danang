package com.hiddendanang.app.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.dataObjects
import com.google.firebase.Timestamp
import com.hiddendanang.app.data.model.Review
import com.hiddendanang.app.data.remote.FirestoreDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.Date

class ReviewRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val dataSource: FirestoreDataSource = FirestoreDataSource()
) {
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    // Lấy tham chiếu Document Review của một User cụ thể: /places/{placeId}/reviews/{userId}
    private fun getUserReviewDocRef(placeId: String, userId: String = currentUserId): DocumentReference {
        return dataSource.getUserReviewDocumentReference(placeId, userId)
    }

    // Lấy tham chiếu Collection: /places/{placeId}/reviews
    private fun getReviewsCollection(placeId: String): CollectionReference {
        return dataSource.getReviewsCollection(placeId)
    }

    // --- 1. Ghi và Cập nhật (Submit/Update Logic) ---

    suspend fun submitOrUpdateReview(
        placeId: String,
        reviewData: Review
    ): Result<Unit> {
        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val placeRef = firestore.collection("places").document(placeId)
        val reviewRef = getUserReviewDocRef(placeId, currentUserId)

        val now = Timestamp(Date())

        return try {
            // SỬ DỤNG TRANSACTION: Đảm bảo tính toán chính xác khi nhiều người review cùng lúc
            firestore.runTransaction { transaction ->
                // BƯỚC 1: ĐỌC DỮ LIỆU (Read)
                val placeSnapshot = transaction.get(placeRef)
                val oldReviewSnapshot = transaction.get(reviewRef)

                // Lấy thông tin hiện tại của Place
                val currentRatingSummary = placeSnapshot.get("rating_summary") as? Map<String, Any> ?: emptyMap()
                val currentCount = (currentRatingSummary["count"] as? Number)?.toLong() ?: 0L
                val currentAvg = (currentRatingSummary["average"] as? Number)?.toDouble() ?: 0.0
                
                val currentDistribution = (currentRatingSummary["distribution"] as? MutableMap<String, Long>) ?: mutableMapOf(
                    "1" to 0L, "2" to 0L, "3" to 0L, "4" to 0L, "5" to 0L
                )

                // BƯỚC 2: TÍNH TOÁN (Calculate)
                val newRatingValue = reviewData.rating.toInt() 
                var newCount = currentCount
                var newTotalScore = currentAvg * currentCount 

                val isUpdate = oldReviewSnapshot.exists()

                if (isUpdate) {
                    val oldRatingValue = oldReviewSnapshot.getDouble("rating")?.toInt() ?: 0
                    newTotalScore = newTotalScore - oldRatingValue + newRatingValue
                    val oldDistCount = currentDistribution[oldRatingValue.toString()] ?: 0L
                    val newDistCount = currentDistribution[newRatingValue.toString()] ?: 0L
                    if (oldDistCount > 0) currentDistribution[oldRatingValue.toString()] = oldDistCount - 1
                    currentDistribution[newRatingValue.toString()] = newDistCount + 1

                } else {
                    newCount += 1
                    newTotalScore += newRatingValue
                    val distCount = currentDistribution[newRatingValue.toString()] ?: 0L
                    currentDistribution[newRatingValue.toString()] = distCount + 1
                }

                val newAvg = if (newCount > 0) newTotalScore / newCount else 0.0
                val roundedAvg = (newAvg * 10).toInt() / 10.0

                // BƯỚC 3: GHI REVIEW (Write Review)
                // QUAN TRỌNG: Thêm place_id vào data ghi xuống
                val dataToWrite = reviewData.copy(
                    user_id = currentUserId,
                    place_id = placeId, // Lưu place_id để query Collection Group
                    created_at = if (isUpdate) oldReviewSnapshot.getTimestamp("created_at") else now,
                    updated_at = now,
                    is_edited = isUpdate
                )
                transaction.set(reviewRef, dataToWrite)

                // BƯỚC 4: CẬP NHẬT PLACE (Update Place)
                val newRatingSummary = mapOf(
                    "average" to roundedAvg,
                    "count" to newCount,
                    "distribution" to currentDistribution
                )
                transaction.update(placeRef, "rating_summary", newRatingSummary)

            }.await()

            Log.d("ReviewRepository", "✅ Submit review & Update stats thành công!")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("ReviewRepository", "❌ Lỗi Transaction: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    // --- 1.5. XÓA Review (Và tính lại điểm) ---
    suspend fun deleteReview(placeId: String): Result<Unit> {
        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val placeRef = firestore.collection("places").document(placeId)
        val reviewRef = getUserReviewDocRef(placeId, currentUserId)

        return try {
            firestore.runTransaction { transaction ->
                val placeSnapshot = transaction.get(placeRef)
                val reviewSnapshot = transaction.get(reviewRef)

                if (!reviewSnapshot.exists()) return@runTransaction

                // 1. Lấy data cũ
                val currentRatingSummary = placeSnapshot.get("rating_summary") as? Map<String, Any> ?: emptyMap()
                val currentCount = (currentRatingSummary["count"] as? Number)?.toLong() ?: 0L
                val currentAvg = (currentRatingSummary["average"] as? Number)?.toDouble() ?: 0.0
                val currentDistribution = (currentRatingSummary["distribution"] as? MutableMap<String, Long>) ?: mutableMapOf()

                val oldRatingValue = reviewSnapshot.getDouble("rating")?.toInt() ?: 0

                // 2. Tính toán trừ đi
                var newCount = currentCount - 1
                var newTotalScore = (currentAvg * currentCount) - oldRatingValue
                
                if (newCount < 0) newCount = 0
                if (newTotalScore < 0) newTotalScore = 0.0

                // Giảm Distribution
                val oldDistCount = currentDistribution[oldRatingValue.toString()] ?: 0L
                if (oldDistCount > 0) currentDistribution[oldRatingValue.toString()] = oldDistCount - 1

                val newAvg = if (newCount > 0) newTotalScore / newCount else 0.0
                val roundedAvg = (newAvg * 10).toInt() / 10.0

                // 3. Xóa Review
                transaction.delete(reviewRef)

                // 4. Cập nhật Place
                val newRatingSummary = mapOf(
                    "average" to roundedAvg,
                    "count" to newCount,
                    "distribution" to currentDistribution
                )
                transaction.update(placeRef, "rating_summary", newRatingSummary)
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 2. Đọc (Read Logic) ---
    fun getUserReviewStream(placeId: String): Flow<Review?> {
        return getUserReviewDocRef(placeId, currentUserId)
            .dataObjects<Review>()
    }

    fun getAllReviewsStreamForPlace(placeId: String): Flow<List<Review>> {
        return getReviewsCollection(placeId)
            .dataObjects<Review>()
    }
    
    // Lấy danh sách review CỦA TÔI (sử dụng Collection Group Query)
    fun getMyReviewsStream(): Flow<List<Review>> {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        return db.collectionGroup("reviews")
            .whereEqualTo("user_id", currentUserId)
            .orderBy("created_at", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .dataObjects<Review>()
    }
}