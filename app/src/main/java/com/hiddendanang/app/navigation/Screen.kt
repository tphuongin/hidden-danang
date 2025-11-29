package com.hiddendanang.app.navigation

import com.hiddendanang.app.data.model.goongmodel.Location

sealed class Screen(val route: String, val title: String) {
    object Splash : Screen("splash", "Splash")
    object HomePage: Screen("homepage", "Home")
    object Login: Screen("login", "Login")
    object Register: Screen("register", "Register")
    object Favorite: Screen("favorite", "Favorite")
    object Profile: Screen("profile", "Profile")
    object Admin : Screen("admin", "Admin Dashboard")
    object AddPlace : Screen("add_place", "Add Place")
    object MyReviews : Screen("my_reviews", "My Reviews")
    object AccountSetting : Screen("account_setting", "Account Settings")

    object DetailPlace: Screen("detail-place/{id}", "Detail Place"){
        fun createRoute(id: String): String = "detail-place/${id}"
    }
    object Search: Screen("search", "Search")
    object Map : Screen(
        "map?destLat={destLat}&destLng={destLng}",
        "Map"
    ) {
        fun createRoute(
            destination: Location?
        ): String {
            return "map?&destLat=${destination?.lat}&destLng=${destination?.lng}"
        }
    }
    object AllReviews: Screen("all-reviews/{id}", "All Reviews"){
        fun createRoute(id: String): String = "all-reviews/${id}"
    }



}