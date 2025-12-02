package com.hiddendanang.app.ui.screen.profile.components

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.composables.icons.lucide.*
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.theme.Dimens
import java.util.*

data class SettingItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val isDestructive: Boolean = false
)

@Composable
fun LanguageSelectionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(R.string.select_language),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column {
                    val languages = listOf("English" to "en", "Vietnamese" to "vi")
                    languages.forEach { (language, code) ->
                        Text(
                            text = language,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLanguageSelected(code)
                                    onDismiss()
                                }
                                .padding(vertical = Dimens.PaddingMedium),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}

private fun updateAppLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

@Composable
fun SettingsSection(
    onLogout: () -> Unit,
    onPostLocation: () -> Unit = {},
    onReviewManagement: () -> Unit = {},
    onAccountSetting: () -> Unit = {},
    onLanguage: (String) -> Unit = {},
    onAdminDashboard: () -> Unit = {}, // Thêm callback này
    isAdmin: Boolean = false // Thêm flag này
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    // Set default language to Vietnamese
    LaunchedEffect(Unit) {
        updateAppLocale(context, "vi")
        onLanguage("vi")
    }

    // Lấy string resources
    val logoutText = stringResource(R.string.logout_title)
    val postLocationText = stringResource(R.string.post_new_location)
    val reviewManagementText = stringResource(R.string.review_management)
    val accountSettingText = stringResource(R.string.account_setting)
    val languageText = stringResource(R.string.language)
    val adminDashboardText = stringResource(R.string.admin_dashboard)

    // Xây dựng danh sách settings
    val settings = mutableListOf<SettingItem>()

    if (isAdmin) {
        settings.add(SettingItem(adminDashboardText, Lucide.Shield))
    }
    settings.add(SettingItem(postLocationText, Lucide.MapPinPlusInside))
    settings.add(SettingItem(reviewManagementText, Lucide.Star))
    settings.add(SettingItem(accountSettingText, Lucide.UserCog))
    settings.add(SettingItem(languageText, Lucide.Languages))
    settings.add(SettingItem(logoutText, Lucide.LogOut, isDestructive = true))


    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.logout_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.logout_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    border = BorderStroke(
                        width = Dimens.StrokeMedium,
                        color = MaterialTheme.colorScheme.error
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.logout_title))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    LanguageSelectionDialog(
        showDialog = showLanguageDialog,
        onDismiss = { showLanguageDialog = false },
        onLanguageSelected = { selectedLanguageCode ->
            Log.d("SettingsSection", "Selected language code: $selectedLanguageCode")
            updateAppLocale(context, selectedLanguageCode)
            onLanguage(selectedLanguageCode)
        }
    )

    Card(
        shape = RoundedCornerShape(Dimens.CornerXLarge),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.ElevationMedium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingLarge)
    ) {
        Column {
            settings.forEachIndexed { index, item ->
                SettingRow(
                    item = item,
                    onClick = {
                        when (item.title) {
                            logoutText -> showLogoutDialog = true
                            postLocationText -> onPostLocation()
                            reviewManagementText -> onReviewManagement()
                            accountSettingText -> onAccountSetting()
                            languageText -> showLanguageDialog = true
                            adminDashboardText -> onAdminDashboard()
                        }
                    },
                    showDivider = index != settings.lastIndex
                )
            }
        }
    }
}

@Composable
fun SettingRow(
    item: SettingItem,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    val iconColor = if (item.isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    val textColor = if (item.isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = Dimens.PaddingMediumLarge, vertical = Dimens.PaddingLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimens.ButtonSmall)
                        .background(
                            color = iconColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(Dimens.CornerMediumLarge)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(Dimens.IconSmall)
                    )
                }
                Spacer(Modifier.width(Dimens.SpaceMedium))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    fontWeight = if (item.isDestructive) FontWeight.Medium else FontWeight.Normal
                )
            }
            Icon(
                imageVector = Lucide.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(Dimens.IconTiny)
            )
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = Dimens.PaddingXXXLarge),
                thickness = Dimens.StrokeSmall,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        }
    }
}