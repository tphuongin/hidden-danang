package com.hiddendanang.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.util.UUID

data class ChatSession(
    @DocumentId
    val id: String = "",
    val title: String = "New Itinerary",
    val user_id: String = "",
    val created_at: Timestamp = Timestamp.now(),
    val updated_at: Timestamp = Timestamp.now()
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String = "user", // "user" or "model"
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now()
)