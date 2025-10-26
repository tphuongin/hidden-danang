package com.hiddendanang.app.navigation

sealed class Screen(val route: String, val title: String) {
    object Splash : Screen("splash", "Splash")
    object HomePage: Screen("homepage", "Home")
    object Map: Screen("map", "Map")
    object Favorite: Screen("favorite", "Favorite")
    object Profile: Screen("profile/id", "Profile"){
        fun createRoute(id: String) = "profile/${id}"
    }
    object DetailPlace: Screen("detail-place/{id}", "Detail Place"){
        fun createRoute(id: String): String = "detail-place/${id}"
    }
}