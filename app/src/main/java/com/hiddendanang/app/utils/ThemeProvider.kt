package com.hiddendanang.app.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.MutableState
import com.hiddendanang.app.utils.constants.AppThemeMode

val LocalThemePreference = compositionLocalOf<MutableState<AppThemeMode>> {
    error("No theme preference provided")
}