package com.hiddendanang.app.navigation

sealed class Screen(val route: String, val title: String) {
    object HomePage: Screen("homepage", "Home")
    object Map: Screen("map", "Map")
    object Favorite: Screen("favorite", "Favorite")
    object Profile: Screen("profile", "Profile")
}