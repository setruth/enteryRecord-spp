package com.setruth.entityflowrecord.ui.pages.setting.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.NotificationAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme

// 其余设置信息
data class OtherSettingItemInfo(
    val icon: ImageVector,
    val title: String,
    val description: String,
)

@Composable
fun OtherSettingsCard(
    modifier: Modifier = Modifier,
    voiceRemindEnable: Boolean,
    noEntranceEnable: Boolean,
    onVoiceRemindChange: (Boolean) -> Unit = {},
    noEntranceEnableChange: (Boolean) -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "其余设置",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))
            SystemSettingItemRow(
                voiceRemindEnable,
                OtherSettingItemInfo(
                    Icons.Outlined.NotificationAdd,
                    "声音提醒",
                    "有人进入时蜂鸣器提醒",
                ),
                onVoiceRemindChange
            )
            Spacer(modifier = Modifier.height(10.dp))
            SystemSettingItemRow(
                noEntranceEnable,
                OtherSettingItemInfo(
                    Icons.Outlined.Block,
                    "禁止进入",
                    "禁止有人进入",
                ),
                noEntranceEnableChange
            )
        }
    }
}

@Composable
fun SystemSettingItemRow(
    checked: Boolean,
    item: OtherSettingItemInfo,
    onChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 图标
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp) // 调整图标大小
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                )
            }
        }
        // 开关
        Switch(
            checked = checked,
            onCheckedChange = { onChange(it) },
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun OtherSettingsCardPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        Box(Modifier.padding(10.dp)) {
            OtherSettingsCard(
                voiceRemindEnable = true,
                noEntranceEnable = true,
            )
        }
    }
}