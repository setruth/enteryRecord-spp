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
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import com.setruth.entityflowrecord.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    currentTitle:String,
    themeMode: ThemeMode,
    themeModeChange: (ThemeModeClickInfo) -> Unit
) {
    var themeModeIconPositionX by remember { mutableFloatStateOf(0f) }
    var themeModeIconPositionY by remember { mutableFloatStateOf(0f) }
    TopAppBar(
        title = { Text(text = currentTitle) },
        actions = {
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