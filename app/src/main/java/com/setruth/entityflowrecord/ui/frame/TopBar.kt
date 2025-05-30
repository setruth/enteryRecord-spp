package com.setruth.entityflowrecord.ui.frame

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import com.setruth.entityflowrecord.data.model.ERROR_COLOR
import com.setruth.entityflowrecord.data.model.SUCCESS_COLOR
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.ui.pages.setting.components.FlowStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    connectState: Boolean,
    currentTitle: String,
    themeMode: ThemeMode,
    themeModeChange: (ThemeModeClickInfo) -> Unit
) {
    var themeModeIconPositionX by remember { mutableFloatStateOf(0f) }
    var themeModeIconPositionY by remember { mutableFloatStateOf(0f) }
    TopAppBar(
        title = { Text(text = currentTitle) },
        actions = {
            val connectStateStr = when (connectState) {
                true -> "已连接"
                false -> "未连接"
            }
            val connectColor = when (connectState) {
                true -> SUCCESS_COLOR
                false -> ERROR_COLOR
            }
            Text(
                connectStateStr,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                color = connectColor
            )
            IconButton(
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    themeModeIconPositionX = coordinates.boundsInRoot().center.x
                    themeModeIconPositionY = coordinates.boundsInRoot().center.y
                },
                onClick = {
                    themeModeChange(
                        ThemeModeClickInfo(
                            themeMode.anotherMode(),
                            themeModeIconPositionX,
                            themeModeIconPositionY
                        )
                    )
                }) {
                AnimatedContent(
                    targetState = themeMode == ThemeMode.DARK,
                ) { targetDarkTheme ->
                    if (targetDarkTheme) {
                        Icon(
                            imageVector = Icons.Outlined.NightsStay,
                            contentDescription = "夜间模式"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.WbSunny,
                            contentDescription = "白天模式"
                        )
                    }
                }
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "白天模式"
                )
            }
        },
    )
}