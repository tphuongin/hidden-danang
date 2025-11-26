package com.hiddendanang.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Review(
    @DocumentId
    val id: String? = null,

    val rating: Int = 0,
    val comment: String = "",

    val user_id: String = "",
    val user_name: String = "",
    val user_photo_url: String = "",

    val photos: List<String> = emptyList(),

    val created_at: Timestamp? = null,
    val updated_at: Timestamp? = null,
    val is_edited: Boolean = false,
    val status: String = "active"
)