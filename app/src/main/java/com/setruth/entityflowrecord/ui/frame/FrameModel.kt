package com.setruth.entityflowrecord.ui.frame

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.setruth.entityflowrecord.model.ThemeMode

data class NavItem(
    val route: String,
    val label: String,
    val topBarTitle:String,
    val icon: ImageVector,
)
data class ThemeModeClickInfo(
    val newMode: ThemeMode,
    val clickX:Float,
    val clickY:Float
)
val navItems = listOf(
    NavItem("home", "首页","智能人员流动总览", Icons.Outlined.Home),
    NavItem("notifications", "记录","人员流动历史记录",  Icons.Outlined.Notifications),
    NavItem("settings", "设置", "智能平台设置",Icons.Outlined.Settings)
)