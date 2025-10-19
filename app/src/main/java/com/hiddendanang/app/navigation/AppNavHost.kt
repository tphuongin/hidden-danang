package com.hiddendanang.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hiddendanang.app.ui.model.Place
import com.hiddendanang.app.ui.screen.detail.DetailScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
//    NavHost(
//        navController = navController,
//        startDestination = Screen.HomePage.route,
//        modifier = modifier
//    ) {
//       composable(Screen.HomePage.route) {
//            HomeScreen(navController = navController)
//      }
//    }
    val dummyPlace = Place(
        name = "Hidden Đà Nẵng Coffee",
        address = "123 Nguyễn Văn Linh, Đà Nẵng",
        images = listOf(
            "https://danangfantasticity.com/wp-content/uploads/2018/10/cau-rong-top-20-cay-cau-ky-quai-nhat-the-gioi-theo-boredom-therapy-02.jpg",
            "https://danangfantasticity.com/wp-content/uploads/2018/10/cau-rong-top-20-cay-cau-ky-quai-nhat-the-gioi-theo-boredom-therapy-02.jpg"
        ),
        rating = 4.8f,
        reviewCount = 215,
        category = "Coffee Shop",
        hours = "08:00 - 22:00",
        priceRange = "30.000đ - 70.000đ",
        distance = "1.2 km",
        description = "Quán cafe phong cách vintage, view đẹp, phù hợp để làm việc và gặp gỡ bạn bè.",
        reviews = emptyList(), // Hoặc tạo vài Review mẫu nếu muốn
        image = "https://example.com/thumbnail.jpg"
    )

    NavHost(
        navController = navController,
        startDestination = Screen.HomePage.route,
        modifier = modifier

    ) {
        composable(Screen.HomePage.route) {
            DetailScreen(
                navController = navController,
                place = dummyPlace,               // <-- Truyền Place cụ thể // <-- Ví dụ mặc định
                onToggleFavortie = { /* TODO */ },     // <-- Callback
                onCLose = { navController.popBackStack() }, // <-- Đóng màn // <-- Hoặc danh sách thực tế
                modifier = Modifier
            )
        }
    }
}