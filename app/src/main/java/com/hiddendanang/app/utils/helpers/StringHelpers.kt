package com.hiddendanang.app.utils.helpers

fun String.capitalizeFirstOnly(): String =
    this.lowercase().replaceFirstChar { it.uppercaseChar() }