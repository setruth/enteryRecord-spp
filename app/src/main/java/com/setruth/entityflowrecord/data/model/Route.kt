package com.setruth.entityflowrecord.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

interface BaseNavItem {
    val route: String
    val label: String
}

data class MainNavItem(
    override val route: String,
    override val label: String,
    val topBarTitle: String,
    val icon: ImageVector,
) : BaseNavItem

data class AppNavItem(
    override val route: String,
    override val label: String,
) : BaseNavItem

val mainNavItems = listOf(
    MainNavItem("home", "首页", "智能人员流动总览", Icons.Outlined.Home),
    MainNavItem("notifications", "记录", "人员流动历史记录", Icons.Outlined.Notifications),
    MainNavItem("settings", "设置", "智能平台设置", Icons.Outlined.Settings)
)
val appNavItems = listOf(
    AppNavItem("splash", "开屏页面"),
    AppNavItem("main", "主导航"),
    AppNavItem("devices", "设备列表"),
)