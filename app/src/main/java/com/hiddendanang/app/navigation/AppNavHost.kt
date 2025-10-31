package com.hiddendanang.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import com.hiddendanang.app.ui.screen.auth.LoginScreen
import com.hiddendanang.app.ui.screen.auth.RegisterScreen
import com.hiddendanang.app.ui.screen.detail.DetailScreen
import com.hiddendanang.app.ui.screen.favorite.FavoriteScreen
import com.hiddendanang.app.ui.screen.home.HomePageScreen
import com.hiddendanang.app.ui.screen.profile.ProfileScreen
import com.hiddendanang.app.ui.screen.splash.SplashScreen


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.HomePage.route) {
            HomePageScreen(navController = navController)
        }
        composable (
            Screen.DetailPlace.route,
            arguments = listOf(
                navArgument("id"){
                    type = NavType.StringType
                }
            )
        ){ navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getString("id")
            if(id != null)
                DetailScreen(navController, id)
        }
        composable(Screen.Favorite.route) {
            FavoriteScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
    }
}