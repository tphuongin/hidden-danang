package com.hiddendanang.app.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp
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
    val count: Int = 0,

)

data class ImageDetail(
    val url: String = "",
    val placeholder_url: String? = null
)

data class TimeDetail(
    val open: String = "", // Format HH:mm
    val close: String = "", // Format HH:mm
    val is_closed: Boolean = false
)

data class OpeningHours(
    val mon: TimeDetail = TimeDetail(),
    val tue: TimeDetail = TimeDetail(),
    val wed: TimeDetail = TimeDetail(),
    val thu: TimeDetail = TimeDetail(),
    val fri: TimeDetail = TimeDetail(),
    val sat: TimeDetail = TimeDetail(),
    val sun: TimeDetail = TimeDetail()
)

data class PriceRange(
    val min: Int = 0,
    val max: Int = 0,
    val currency: String = "" // Ví dụ: "VND"
)


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

    val price_range_detail: PriceRange = PriceRange(), // Đổi tên để tránh trùng với PriceRange data class
    val opening_hours: OpeningHours = OpeningHours(),

    val price_range: Int = 0,
    val price_indicator: String = "",

    val status: String = "",
    val is_verified: Boolean = false,
    val popularity_score: Double = 0.0,
    val created_at: Timestamp? = null
)