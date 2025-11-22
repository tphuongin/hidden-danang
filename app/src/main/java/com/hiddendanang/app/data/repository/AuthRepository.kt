package com.hiddendanang.app.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.hiddendanang.app.data.model.User
import com.hiddendanang.app.data.remote.FirestoreDataSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth by lazy(LazyThreadSafetyMode.NONE) { FirebaseAuth.getInstance() }
    private val dataSource: FirestoreDataSource by lazy(LazyThreadSafetyMode.NONE) { FirestoreDataSource() }
    val currentUserStream = callbackFlow<FirebaseUser?> {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        FirebaseAuth.getInstance().addAuthStateListener(listener)
        awaitClose { FirebaseAuth.getInstance().removeAuthStateListener(listener) }
    }

    //Lắng nghe document User từ Firestore.
    //Bất cứ khi nào data (vd: display_name) thay đổi, Flow này sẽ phát ra data mới.
    fun getUserProfileStream(uid: String): Flow<User?> {
        return dataSource.getUserDocumentReference(uid).snapshots().map { snapshot ->
            // Chuyển đổi snapshot thành User object (đã có @DocumentId)
            snapshot.toObject<User>()
        }
    }

    fun logout() {
        auth.signOut()
    }
    suspend fun registerUser(fullname : String, email: String, password: String): Result<FirebaseUser> {
        return try {
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
            val userUid = user?.uid ?: throw IllegalStateException("User creation failed, UID is null.")


            val newUserProfile = User(
                uid = userUid,
                email = email,
                display_name = fullname,
                created_at = com.google.firebase.Timestamp.now()
            )
            dataSource.getUserDocumentReference(userUid).set(newUserProfile).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // SỬA: Thay đổi kiểu trả về từ Result<FirebaseUser> thành Result<User>
    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            // Bước 1: Đăng nhập vào Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException("UID is null")

            // Bước 2: Dùng UID đó lấy ngay dữ liệu từ Firestore
            // (Lúc này lấy luôn được preferences, theme, bio...)
            val documentSnapshot = dataSource.getUserDocumentReference(uid).get().await()

            // Bước 3: Parse sang object User của bạn
            val customUser = documentSnapshot.toObject<User>()
                ?: throw IllegalStateException("User data not found in Firestore")

            // Trả về User đầy đủ thông tin
            Result.success(customUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // Cập nhật thông tin profile.
    // Các tham số để null nghĩa là không muốn thay đổi trường đó.
    suspend fun updateUserProfile(
        uid: String,
        displayName: String? = null,
        bio: String? = null,
        photoUrl: String? = null,
        theme: String? = null
    ): Result<Unit> {
        return try {
            // 1. Chuẩn bị dữ liệu để update lên Firestore
            val updates = mutableMapOf<String, Any>()

            // Chỉ đưa vào map những trường có giá trị (không null)
            displayName?.let { updates["display_name"] = it }
            bio?.let { updates["bio"] = it }
            photoUrl?.let { updates["photo_url"] = it }
            theme?.let { updates["preferences.theme"] = it }

            if (updates.isNotEmpty()) {
                // Gọi lệnh update (chỉ thay đổi các trường được chỉ định, giữ nguyên các trường khác)
                dataSource.getUserDocumentReference(uid).update(updates).await()
            }

            // 2. Cập nhật song song lên FirebaseAuth (để đồng bộ Authentication Profile)
            // Bước này quan trọng để khi gọi auth.currentUser.displayName thì ra tên mới ngay
            val firebaseUser = auth.currentUser
            if (firebaseUser != null && (displayName != null || photoUrl != null)) {
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder().apply {
                    displayName?.let { setDisplayName(it) }
                    photoUrl?.let { setPhotoUri(android.net.Uri.parse(it)) }
                }.build()

                firebaseUser.updateProfile(profileUpdates).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
}