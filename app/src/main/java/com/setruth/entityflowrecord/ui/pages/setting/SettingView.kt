package com.setruth.entityflowrecord.ui.pages.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MultilineChart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.NotificationAdd
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setruth.entityflowrecord.model.ThemeMode
import com.setruth.entityflowrecord.ui.frame.MainFrame
import com.setruth.entityflowrecord.ui.pages.setting.components.CapacityConfigCard
import com.setruth.entityflowrecord.ui.pages.setting.components.FlowStatus
import com.setruth.entityflowrecord.ui.pages.setting.components.SystemSettingItem
import com.setruth.entityflowrecord.ui.pages.setting.components.SystemSettingsCard
import com.setruth.entityflowrecord.ui.pages.setting.components.ThresholdConfigCard
import com.setruth.entityflowrecord.ui.pages.setting.components.ThresholdItem
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme

@Composable
fun SettingView() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            CapacityConfigCard()
        }
        item{
            ThresholdConfigCard(
                thresholdItems = listOf(
                    ThresholdItem(FlowStatus.GREEN, "绿灯状态", "正常通行，允许进入", "大于0人"),
                    ThresholdItem(FlowStatus.YELLOW, "黄灯状态", "谨慎通行，注意人数", "大于35人"),
                    ThresholdItem(FlowStatus.RED, "红灯状态", "禁止进入，已达上限", "大于70人")
                )
            )
        }
        item {
            SystemSettingsCard(
                settingsItems = listOf(
                    SystemSettingItem(
                        Icons.Outlined.NotificationAdd,
                        "声音提醒",
                        "有人进入时蜂鸣器提醒",
                        true
                    ),
                    SystemSettingItem(
                        Icons.Outlined.Block,
                        "停止禁入",
                        "启用后达到最大容量将禁止进入",
                        false
                    )
                )
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun HomeViewPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        MainFrame(2)
    }
}