package com.setruth.entityflowrecord.data.model

import androidx.compose.ui.graphics.Color

enum class MaskAnimModel {
    EXPEND,
    SHRINK,
}

enum class ThemeMode {
    DARK,
    LIGHT;

    fun anotherMode() = when (this) {
        DARK -> LIGHT
        LIGHT -> DARK
    }
}
enum class FLowType{
    ENTRY,
    EXIT
}
val SUCCESS_COLOR= Color(0xFF66BB6A)
val ERROR_COLOR= Color(0xFFEF5350)