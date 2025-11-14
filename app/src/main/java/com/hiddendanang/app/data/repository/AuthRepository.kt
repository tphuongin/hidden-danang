// File: AuthRepository.kt (Trong package repository)
package com.hiddendanang.app.data.repository

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

    // --- HÀM MỚI 2: Lấy thông tin User từ Firestore (theo thời gian thực) ---
    /**
     * Lắng nghe document User từ Firestore.
     * Bất cứ khi nào data (vd: display_name) thay đổi, Flow này sẽ phát ra data mới.
     */
    fun getUserProfileStream(uid: String): Flow<User?> {
        return dataSource.getUserDocumentReference(uid).snapshots().map { snapshot ->
            // Chuyển đổi snapshot thành User object (đã có @DocumentId)
            snapshot.toObject<User>()
        }
    }

    // --- HÀM MỚI 3: Đăng xuất ---
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

    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val user = auth.signInWithEmailAndPassword(email, password).await().user
            Result.success(user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
}