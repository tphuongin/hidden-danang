package com.hiddendanang.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hiddendanang.app.navigation.Screen

import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        MapLibre.getInstance(this, "", WellKnownTileServer.MapLibre)

        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiddenDaNangApp()
        }
    }
}

@Composable
fun HiddenDaNangApp(){
    val themePreference = remember { mutableStateOf(AppThemeMode.SYSTEM) }
    val navController = rememberNavController()

    CompositionLocalProvider(LocalThemePreference provides themePreference) {
        HiddenDaNangTheme(themeApp = themePreference.value) {
            Scaffold(
                bottomBar = {
                    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                    if (currentRoute != Screen.Splash.route) {
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