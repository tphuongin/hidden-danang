package com.hiddendanang.app.data.model

import com.google.firebase.firestore.DocumentId

data class Subcategory(
    val id: String = "",
    val name: String = "",
    val icon: String = ""
)


data class Category(
    @DocumentId
    val id: String? = null,

    val name: String = "",
    val name_en: String = "",
    val icon: String = "",
    val color: String = "",
    val description: String = "",

    val subcategories: List<Subcategory> = emptyList(),

    val sort_order: Int = 0,
    val is_active: Boolean = true,
    val location_count: Int = 0
)
