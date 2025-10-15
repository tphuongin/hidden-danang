package com.hiddendanang.app.navigation

sealed class Screen(val route: String) {
    object HomePage: Screen("HomePage")
}