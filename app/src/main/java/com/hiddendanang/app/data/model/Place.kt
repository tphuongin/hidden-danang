package com.hiddendanang.app.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Coordinates(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val geohash: String = ""
)

data class Address(
    val street: String = "",
    val district: String = "",
    val city: String = "",
    val formatted_address: String = ""
)

data class RatingSummary(
    val average: Double = 0.0,
    val count: Int = 0
)

data class ImageDetail(
    val url: String = "",
    val placeholder_url: String? = null
)

// --- Class mới cho Giá và Giờ mở cửa ---

data class PriceRange(
    val min: Double = 0.0,
    val max: Double = 0.0,
    val currency: String = "VND"
)

data class DailySchedule(
    val open: String = "00:00",
    val close: String = "00:00",
    @get:PropertyName("is_closed")
    val isClosed: Boolean = false
)

data class OpeningHours(
    val mon: DailySchedule? = null,
    val tue: DailySchedule? = null,
    val wed: DailySchedule? = null,
    val thu: DailySchedule? = null,
    val fri: DailySchedule? = null,
    val sat: DailySchedule? = null,
    val sun: DailySchedule? = null
)

// --- Class Place cập nhật ---

data class Place(
    @DocumentId
    val id: String = "",

    val name: String = "",
    val name_lower: String = "",
    val description: String = "",

    val category_id: String = "",
    val subcategory: String = "",
    val tags: List<String> = emptyList(),

    val coordinates: Coordinates = Coordinates(),
    val address: Address = Address(),

    val rating_summary: RatingSummary = RatingSummary(),

    val images: List<ImageDetail> = emptyList(),

    // Đổi từ Int sang PriceRange? và map đúng key trong JSON
    @get:PropertyName("price_range")
    val price_range: PriceRange? = null,

    // Thêm trường mới opening_hours
    @get:PropertyName("opening_hours")
    val opening_hours: OpeningHours? = null,

    val price_indicator: String = "", // $$$

    val status: String = "",
    val is_verified: Boolean = false,
    val popularity_score: Double = 0.0,
    val created_at: Timestamp? = null
)