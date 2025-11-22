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

/**
 * Repository quản lý Đánh giá (Review).
 * Quy tắc: UserID là Document ID của Review để đảm bảo tính duy nhất.
 */
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
        val now = Timestamp(Date())
        val isUpdate = reviewData.created_at != null

        return try {
            val reviewRef = getUserReviewDocRef(placeId, currentUserId)

            // Chuẩn bị dữ liệu: Ghi đè lên Document có ID là UID của User
            val dataToWrite = reviewData.copy(
                user_id = currentUserId,
                created_at = reviewData.created_at ?: now,
                updated_at = now,
                is_edited = isUpdate
            )
            try {
                reviewRef.set(dataToWrite).await()
                Log.d("ReviewRepository", "✅ Firestore set() hoàn tất")
            } catch (e: Exception) {
                Log.e("ReviewRepository", "❌ Lỗi Firestore: ${e.javaClass.simpleName}")
                Log.e("ReviewRepository", "Chi tiết: ${e.message}")
                e.printStackTrace()
                throw e
            }
            // TODO: Bổ sung logic tính toán và cập nhật rating_summary của Place (sau khi làm xong)
            Log.d("ReviewRepository", "Submit thành công!")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 2. Đọc (Read Logic) ---

    /**
     * Lấy Stream đánh giá DUY NHẤT của người dùng hiện tại (Dùng để khởi tạo ReviewForm).
     * Trả về Flow<Review?>
     */
    fun getUserReviewStream(placeId: String): Flow<Review?> {
        // Hàm dataObjects trên Document Reference (vì nó dùng getUserReviewDocRef)
        // tự động trả về Flow<T?> (Tức là Review hoặc null).
        // KHÔNG CẦN .map {} hoặc .firstOrNull
        return getUserReviewDocRef(placeId, currentUserId)
            .dataObjects<Review>()
    }

    /**
     * Lấy Stream tất cả đánh giá cho một địa điểm cụ thể (Dùng cho danh sách ReviewCard).
     * Trả về Flow<List<Review>>
     */
    fun getAllReviewsStreamForPlace(placeId: String): Flow<List<Review>> {
        return getReviewsCollection(placeId)
            .dataObjects<Review>()
    }
}