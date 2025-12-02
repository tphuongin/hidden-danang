package com.hiddendanang.app.data.model

import com.google.firebase.Timestamp

data class NotificationsPreference(
    val new_locations: Boolean = true,
    val reviews: Boolean = true,
    val promotions: Boolean = false
)

data class Preferences(
    val language: String = "vi",
    val theme: String = "system",
    val notifications: NotificationsPreference = NotificationsPreference()
)

data class UserStats(
    val reviews_count: Int = 0,
    val photos_uploaded: Int = 0,
    val locations_added: Int = 0,
    val helpful_votes: Int = 0
)

data class User(
    val uid: String = "",
    val email: String = "",
    val display_name: String = "",
    val photo_url: String = "",
    val bio: String? = null,

    val preferences: Preferences = Preferences(),

    val stats: UserStats = UserStats(),

    val role: String = "user",
    val is_verified: Boolean = false,
    val status: String = "active",

    val created_at: Timestamp? = null,
    val last_login_at: Timestamp? = null,
    val login_count: Int = 0
)