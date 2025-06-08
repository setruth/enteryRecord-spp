package com.setruth.entityflowrecord.data.model

import androidx.compose.ui.graphics.Color

data class ThemeModeClickInfo(
    val newMode: ThemeMode,
    val clickX: Float,
    val clickY: Float
)

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

enum class FLowType {
    ENTRY,
    EXIT
}

enum class TipLight {
    GREEN,
    YELLOW,
    RED,
}

val SUCCESS_COLOR = Color(0xFF66BB6A)
val ERROR_COLOR = Color(0xFFEF5350)

const val SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB"
//默认的配置文件内容
const val DEFAULT_MAX_COUNT = 10
const val DEFAULT_CURRENT_COUNT = 0
const val DEFAULT_IS_FULL_ON = true
const val DEFAULT_IS_BUZZ_ON = true
const val DEFAULT_ALARM_LIGHT = 4
const val DEFAULT_ERR_LIGHT = 6