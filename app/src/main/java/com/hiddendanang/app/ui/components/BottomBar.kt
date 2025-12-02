package com.hiddendanang.app.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.composables.icons.lucide.BookHeart
import com.composables.icons.lucide.CircleUser
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPinned
import com.hiddendanang.app.R
import com.hiddendanang.app.navigation.Screen
import com.hiddendanang.app.ui.theme.Dimens

data class BottomNavItem(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
)

val BottomNavItems = listOf(
    BottomNavItem(Screen.HomePage.route, R.string.bottom_nav_home, Lucide.House),
    BottomNavItem(Screen.Map.route, R.string.bottom_nav_map, Lucide.MapPinned),
    BottomNavItem(Screen.Favorite.route, R.string.bottom_nav_favorite, Lucide.BookHeart),
    BottomNavItem(Screen.Profile.route, R.string.bottom_nav_profile, Lucide.CircleUser),
)

@Composable
fun BottomBar(navController: NavHostController) {
    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .height(Dimens.BottomBarMaxHeight)
            .padding(horizontal = Dimens.PaddingLarge, vertical = Dimens.PaddingSmall)
    ) {
        BottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.1f else 1f,
                animationSpec = tween(durationMillis = 300),
                label = "iconScale"
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingTiny),
                        modifier = Modifier
                            .clip(RoundedCornerShape(Dimens.CornerXLarge))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface
                            )
                            .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(id = item.titleRes),
                            modifier = Modifier
                                .size(Dimens.IconMedium)
                                .scale(scale),
                            tint = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = stringResource(id = item.titleRes),
                            fontSize = Dimens.TextSmall,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.Transparent,
                    selectedTextColor = Color.Transparent,
                    unselectedTextColor = Color.Transparent,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

