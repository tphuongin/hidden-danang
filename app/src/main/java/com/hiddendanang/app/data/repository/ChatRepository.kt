package com.hiddendanang.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.hiddendanang.app.data.model.ChatMessage
import com.hiddendanang.app.data.model.ChatSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChatRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    private fun getSessionsCollection() = 
        firestore.collection("users").document(currentUserId).collection("chat_sessions")

    private fun getMessagesCollection(sessionId: String) = 
        getSessionsCollection().document(sessionId).collection("messages")

    // --- Sessions ---
    
    fun getSessionsStream(): Flow<List<ChatSession>> {
        return getSessionsCollection()
            .orderBy("updated_at", Query.Direction.DESCENDING)
            .dataObjects<ChatSession>()
    }

    suspend fun createSession(title: String = "New Plan"): String {
        val id = UUID.randomUUID().toString()
        val session = ChatSession(
            id = id,
            title = title,
            user_id = currentUserId
        )
        getSessionsCollection().document(id).set(session).await()
        return id
    }

    // --- Messages ---

    fun getMessagesStream(sessionId: String): Flow<List<ChatMessage>> {
        return getMessagesCollection(sessionId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .dataObjects<ChatMessage>()
    }

    suspend fun addMessage(sessionId: String, message: ChatMessage) {
        getMessagesCollection(sessionId).document(message.id).set(message).await()
        
        // Update session timestamp and potentially title (if first message)
        getSessionsCollection().document(sessionId).update(
            "updated_at", message.timestamp
        ).await()
    }
}