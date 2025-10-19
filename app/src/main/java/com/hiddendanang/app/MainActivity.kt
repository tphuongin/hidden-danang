package com.hiddendanang.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hiddendanang.app.navigation.AppNavHost
import com.hiddendanang.app.ui.theme.HiddenDaNangTheme
import com.hiddendanang.app.utils.constants.AppThemeMode

class   MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themePreference = remember { mutableStateOf(AppThemeMode.SYSTEM) }
            HiddenDaNangTheme(themeApp = themePreference.value ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    AppNavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
            }
        }
    }
}
