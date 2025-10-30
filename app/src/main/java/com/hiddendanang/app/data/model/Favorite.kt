package com.hiddendanang.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Favorite(

    @DocumentId
    val favoriteId: String? = null,

    val location_id: String = "",

    val location_name: String = "",
    val location_category: String = "",
    val location_image: String = "",

    val location_rate: Double = 0.0,

    val user_id: String = "",

    val created_at: Timestamp? = null
)