package com.hiddendanang.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.*
import com.composables.icons.lucide.*
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.utils.LocalThemePreference
import com.hiddendanang.app.utils.constants.AppThemeMode
import com.hiddendanang.app.utils.helpers.capitalizeFirstOnly
import java.net.URL


data class User(
    val name: String,
    val email: String,
    val avatarURL: URL
)

data class SettingItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val themeModes = listOf(
    AppThemeMode.LIGHT,
    AppThemeMode.DARK,
    AppThemeMode.SUNSET
)

//------------------ Main Screen ------------------

@Composable
fun ProfileScreen(navController: NavHostController, user: User?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.PaddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user == null) {
            NotLoggedInView(onLoginClick = { /* TODO: navController.navigate("login") */ })
        } else {
            LoggedInProfile(user)
        }
    }
}

//------------------ Logged In View ------------------

@Composable
fun LoggedInProfile(user: User) {
    val themePreference = LocalThemePreference.current

    ProfileHeader(user)
    Spacer(Modifier.height(Dimens.SpaceSmall))

    ThemeSelector(
        currentTheme = themePreference.value,
        onThemeChange = { themePreference.value = it }
    )

    Spacer(Modifier.height(Dimens.SpaceSmall))

    SettingsSection()
}

@Composable
fun ProfileHeader(user: User) {
    Card(
        shape = RoundedCornerShape(Dimens.CornerXLarge),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.ElevationHigh),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Dimens.PaddingLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.avatarURL,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(Dimens.AvatarLarge)
                    .clip(CircleShape)
                    .background(Color(0xFFEFEFEF))
            )
            Spacer(Modifier.width(Dimens.SpaceMedium))
            Column {
                Text(
                    user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ThemeSelector(currentTheme: AppThemeMode, onThemeChange: (AppThemeMode) -> Unit) {
    Card(
        shape = RoundedCornerShape(Dimens.CornerXLarge),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.ElevationHigh),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(Dimens.PaddingLarge)) {
            Text(
                stringResource(R.string.display_mode),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(Modifier.height(Dimens.SpaceSmall))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)
            ) {
                themeModes.forEach { mode ->
                    val isSelected = currentTheme == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(Dimens.CornerLarge))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(Dimens.CornerLarge)
                            )
                            .clickable { onThemeChange(mode) }
                            .padding(vertical = Dimens.PaddingLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.name.capitalizeFirstOnly(),
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection() {
    val settings = listOf(
        SettingItem(stringResource(R.string.post_new_location), Lucide.MapPinPlusInside),
        SettingItem(stringResource(R.string.review_management), Lucide.Star),
        SettingItem(stringResource(R.string.account_setting), Lucide.UserCog),
        SettingItem(stringResource(R.string.logout), Lucide.LogOut),
    )

    Card(
        shape = RoundedCornerShape(Dimens.CornerXLarge),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.ElevationHigh),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        settings.forEachIndexed { index, item ->
            SettingRow(item)
            if (index != settings.lastIndex)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        }
    }
}

@Composable
fun SettingRow(item: SettingItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: handle navigation */ }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Text(item.title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Icon(Lucide.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}

//------------------ Not Logged In View ------------------

@Composable
fun NotLoggedInView(onLoginClick: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.login))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.PaddingXLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(220.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            stringResource(R.string.welcome),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.welcome_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(Modifier.height(20.dp))
        Button(onClick = onLoginClick, shape = RoundedCornerShape(12.dp)) {
            Text(stringResource(R.string.login_now))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    val navController = rememberNavController()
    val user = User(
        "Nguyễn Văn A",
        "nguyenvana@email.com",
        URL("https://via.placeholder.com/150")
    )
//    ProfileScreen(navController, null)

    // Thêm phần này để cung cấp themePreference
    val themePreference = remember { mutableStateOf(AppThemeMode.SYSTEM) }
    CompositionLocalProvider(LocalThemePreference provides themePreference) {
        com.hiddendanang.app.ui.theme.HiddenDaNangTheme(themeApp = themePreference.value) {
            ProfileScreen(navController, user)
        }
    }
}

