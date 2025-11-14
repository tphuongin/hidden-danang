package com.hiddendanang.app

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.hiddendanang.app.navigation.AppNavHost
import com.hiddendanang.app.ui.components.BottomBar
import com.hiddendanang.app.ui.theme.HiddenDaNangTheme
import com.hiddendanang.app.utils.LocalThemePreference
import com.hiddendanang.app.utils.constants.AppThemeMode
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hiddendanang.app.data.repository.AuthRepository
import com.hiddendanang.app.data.repository.LocationRepository
import com.hiddendanang.app.di.DebugSeeder
import com.hiddendanang.app.navigation.Screen
import com.hiddendanang.app.ui.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        MapLibre.getInstance(this, "", WellKnownTileServer.MapLibre)

        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiddenDaNangApp(
                mainViewModel,
            )
        }
    }
}

@Composable
fun HiddenDaNangApp(
    mainViewModel : MainViewModel,
){
    val themePreference = remember { mutableStateOf(AppThemeMode.SYSTEM) }
    val navController = rememberNavController()
    val currentUser by mainViewModel.currentUser.collectAsState()

    // Lấy route hiện tại để kiểm tra
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Danh sách các màn hình không yêu cầu đăng nhập
    val authScreens = setOf(
        Screen.Splash.route,
        Screen.Login.route,
        Screen.Register.route
    )

    // Tự động điều hướng khi trạng thái Auth thay đổi
    LaunchedEffect(currentUser, currentRoute) {
        if (currentUser == null && currentRoute !in authScreens) {
            // Nếu user là null (đã logout) VÀ đang không ở màn hình auth
            // -> Đưa về trang Login
            navController.navigate(Screen.Login.route) {
                // Xóa tất cả backstack
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    CompositionLocalProvider(LocalThemePreference provides themePreference) {
        HiddenDaNangTheme(themeApp = themePreference.value) {
            Scaffold(
                bottomBar = {
                    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                    val bottomBarRoutes = setOf(
                        Screen.HomePage.route,
                        Screen.Favorite.route,
                        Screen.Profile.route
                    )
                    if (currentRoute in bottomBarRoutes) {
                        BottomBar(navController)
                    }
                }
            ) { paddingValues ->
                AppNavHost(
                    modifier = Modifier.padding(paddingValues),
                    navController = navController,
                )
            }
        }
    }
}