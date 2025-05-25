package com.setruth.entityflowrecord.model

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
    ENTRY, // 进入
    EXIT   // 出去
}