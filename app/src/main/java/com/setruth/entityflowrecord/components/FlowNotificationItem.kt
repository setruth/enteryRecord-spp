package com.setruth.entityflowrecord.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setruth.entityflowrecord.model.FLowType
import com.setruth.entityflowrecord.model.FlowBaseRecord
import com.setruth.entityflowrecord.model.ThemeMode
import com.setruth.entityflowrecord.ui.frame.MainFrame
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlowNotificationItem(modifier: Modifier = Modifier, index: Int, record: FlowBaseRecord) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm:ss") }

    // 根据 FlowType 判断颜色、文本和图标
    val changeColor =
        if (record.type == FLowType.ENTRY) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
    val changeText = if (record.type == FLowType.ENTRY) "+1" else "-1" // 假设每次变化都是 +1 或 -1
    val statusText = if (record.type == FLowType.ENTRY) "进入" else "出去"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // 左右两边内容对齐
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.width(100.dp)
            ) {
                Text(
                    text = "$index",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = record.timestamp.format(timeFormatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

            }

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                var iconModifier = Modifier.size(24.dp)
                if (record.type == FLowType.ENTRY) {
                    iconModifier = iconModifier.rotate(180f)
                }
                Icon(
                    imageVector = Icons.Default.ArrowOutward,
                    contentDescription = statusText,
                    tint = changeColor,
                    modifier = iconModifier
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = changeText,
                    color = changeColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                textAlign = TextAlign.End,
                modifier = Modifier.width(100.dp),
                text = "${record.changeAfterTotalPeople}人",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun HomeViewPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        FlowNotificationItem(index = 1, record = object : FlowBaseRecord {
            override val timestamp: LocalDateTime
                @RequiresApi(Build.VERSION_CODES.O)
                get() = LocalDateTime.now()
            override val type: FLowType
                get() = FLowType.ENTRY
            override val changeAfterTotalPeople: Int
                get() = 12
        })
    }
}