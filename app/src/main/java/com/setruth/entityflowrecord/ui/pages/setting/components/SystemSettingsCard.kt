package com.setruth.entityflowrecord.ui.pages.setting.components


import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MultilineChart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.NotificationAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.ui.frame.MainFrame
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme

// 系统设置项的数据类
data class SystemSettingItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val isChecked: Boolean = false // 用于 Switch 状态
)

@Composable
fun SystemSettingsCard(modifier: Modifier = Modifier, settingsItems: List<SystemSettingItem>) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "硬件设置",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            settingsItems.forEachIndexed { index, item ->
                SystemSettingItemRow(item = item)
                if (index < settingsItems.lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp)) // 行间距
                }
            }
        }
    }
}

@Composable
fun SystemSettingItemRow(item: SystemSettingItem) {
    var checkedState by remember { mutableStateOf(item.isChecked) }

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
            checked = checkedState,
            onCheckedChange = { checkedState = it },
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun SystemSettingsCardPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        Box(Modifier.padding(10.dp)) {
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