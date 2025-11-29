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
import kotlinx.coroutines.flow.map
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
                // Phải đọc Place và Review cũ TRƯỚC khi ghi bất cứ thứ gì
                val placeSnapshot = transaction.get(placeRef)
                val oldReviewSnapshot = transaction.get(reviewRef)

                // Lấy thông tin hiện tại của Place
                val currentRatingSummary = placeSnapshot.get("rating_summary") as? Map<String, Any> ?: emptyMap()
                val currentCount = (currentRatingSummary["count"] as? Number)?.toLong() ?: 0L
                val currentAvg = (currentRatingSummary["average"] as? Number)?.toDouble() ?: 0.0
                // Lấy distribution cũ (để cập nhật biểu đồ 1 sao, 2 sao...)
                val currentDistribution = (currentRatingSummary["distribution"] as? MutableMap<String, Long>) ?: mutableMapOf(
                    "1" to 0L, "2" to 0L, "3" to 0L, "4" to 0L, "5" to 0L
                )

                // BƯỚC 2: TÍNH TOÁN (Calculate)
                val newRatingValue = reviewData.rating.toInt() // Điểm user vừa chấm (vd: 5)
                var newCount = currentCount
                var newTotalScore = currentAvg * currentCount // Tổng điểm tích lũy cũ

                // Kiểm tra xem đây là "Tạo mới" hay "Cập nhật"
                val isUpdate = oldReviewSnapshot.exists()

                if (isUpdate) {
                    // --- TRƯỜNG HỢP CẬP NHẬT REVIEW ---
                    val oldRatingValue = oldReviewSnapshot.getDouble("rating")?.toInt() ?: 0

                    // Trừ điểm cũ ra, cộng điểm mới vào
                    newTotalScore = newTotalScore - oldRatingValue + newRatingValue

                    // Cập nhật Distribution: Giảm số lượng của sao cũ, tăng sao mới
                    val oldDistCount = currentDistribution[oldRatingValue.toString()] ?: 0L
                    val newDistCount = currentDistribution[newRatingValue.toString()] ?: 0L
                    if (oldDistCount > 0) currentDistribution[oldRatingValue.toString()] = oldDistCount - 1
                    currentDistribution[newRatingValue.toString()] = newDistCount + 1

                } else {
                    // --- TRƯỜNG HỢP REVIEW MỚI ---
                    newCount += 1
                    newTotalScore += newRatingValue

                    // Cập nhật Distribution: Tăng sao mới
                    val distCount = currentDistribution[newRatingValue.toString()] ?: 0L
                    currentDistribution[newRatingValue.toString()] = distCount + 1
                }

                // Tính trung bình mới (làm tròn 1 chữ số thập phân)
                val newAvg = if (newCount > 0) newTotalScore / newCount else 0.0
                val roundedAvg = (newAvg * 10).toInt() / 10.0

                // BƯỚC 3: GHI REVIEW (Write Review)
                val dataToWrite = reviewData.copy(
                    user_id = currentUserId,
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

            }.await() // Chờ Transaction hoàn tất

            Log.d("ReviewRepository", "✅ Submit review & Update stats thành công!")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("ReviewRepository", "❌ Lỗi Transaction: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // --- 2. Đọc (Read Logic) ---
    fun getUserReviewStream(placeId: String): Flow<Review?> {
        // Hàm dataObjects trên Document Reference (vì nó dùng getUserReviewDocRef)
        // tự động trả về Flow<T?> (Tức là Review hoặc null).
        // KHÔNG CẦN .map {} hoặc .firstOrNull
        return getUserReviewDocRef(placeId, currentUserId)
            .dataObjects<Review>()
    }

    fun getAllReviewsStreamForPlace(placeId: String): Flow<List<Review>> {
        return getReviewsCollection(placeId)
            .dataObjects<Review>()
    }
}