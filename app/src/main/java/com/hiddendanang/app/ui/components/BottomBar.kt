package com.hiddendanang.app.ui.components

import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.composables.icons.lucide.BookHeart
import com.composables.icons.lucide.CircleUser
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPinned
import com.hiddendanang.app.navigation.Screen
import com.hiddendanang.app.ui.theme.Dimens

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)
val BottomNavItems = listOf<BottomNavItem>(
    BottomNavItem(Screen.HomePage.route, Screen.HomePage.title, Lucide.House),
    BottomNavItem(Screen.Map.route, Screen.Map.title, Lucide.MapPinned),
    BottomNavItem(Screen.Favorite.route, Screen.Favorite.title, Lucide.BookHeart),
    BottomNavItem(Screen.Profile.route, Screen.Profile.title, Lucide.CircleUser),
)
@Composable
fun BottomBar(navController: NavHostController){
    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(0.8f),
    ) {
        for(item in BottomNavItems){
            Log.e("item", item.toString())
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if(currentRoute != item.route){
                            navController.navigate(item.route){
                                popUpTo(navController.graph.startDestinationId){
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(item.icon, item.title)
                    },
                    label = {
                        Text(
                            item.title,
                            fontSize = Dimens.TextSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
        }
        }
}

@Preview
@Composable
fun show(){
    val navController: NavHostController = rememberNavController()
    BottomBar(navController)
}