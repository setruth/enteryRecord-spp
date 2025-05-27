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
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setruth.entityflowrecord.model.ThemeMode
import com.setruth.entityflowrecord.ui.frame.MainFrame
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme


// 流量类型（对应红黄绿灯）
enum class FlowStatus {
    GREEN, YELLOW, RED
}

// 阈值配置项的数据类
data class ThresholdItem(
    val status: FlowStatus,
    val title: String,
    val description: String,
    val range: String
)
@Composable
fun ThresholdConfigCard(modifier: Modifier = Modifier, thresholdItems: List<ThresholdItem>) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "阈值配置",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            thresholdItems.forEachIndexed { index, item ->
                ThresholdItemRow(item = item)
                if (index < thresholdItems.lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp)) // 行间距
                }
            }
        }
    }
}
@Composable
fun ThresholdItemRow(item: ThresholdItem) {
    val indicatorColor = when (item.status) {
        FlowStatus.GREEN -> Color(0xFF66BB6A) // 绿色
        FlowStatus.YELLOW -> Color(0xFFFFCA28) // 黄色
        FlowStatus.RED -> Color(0xFFEF5350) // 红色
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 圆形指示器
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )
                Spacer(modifier = Modifier.width(12.dp))
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
            OutlinedButton(
                onClick = {}
            ) {
                Text(
                    text = item.range,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun  ThresholdConfigCardPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        Box(Modifier.padding(10.dp)) {
            ThresholdConfigCard(
                thresholdItems = listOf(
                    ThresholdItem(FlowStatus.GREEN, "绿灯状态", "正常通行，允许进入", "0 - 35人"),
                    ThresholdItem(FlowStatus.YELLOW, "黄灯状态", "谨慎通行，注意人数", "35 - 45人"),
                    ThresholdItem(FlowStatus.RED, "红灯状态", "禁止进入，已达上限", "45+人")
                )
            )
        }
    }
}