// File: AuthRepository.kt (Trong package repository)
package com.hiddendanang.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hiddendanang.app.data.model.User
import com.hiddendanang.app.data.remote.FirestoreDataSource
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dataSource: FirestoreDataSource = FirestoreDataSource()

    suspend fun registerUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
            val userUid = user?.uid ?: throw IllegalStateException("User creation failed, UID is null.")

            val newUserProfile = User(
                uid = userUid,
                email = user.email
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