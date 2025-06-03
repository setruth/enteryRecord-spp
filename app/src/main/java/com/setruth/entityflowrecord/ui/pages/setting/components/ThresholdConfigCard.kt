package com.setruth.entityflowrecord.ui.pages.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setruth.entityflowrecord.data.di.appModule
import com.setruth.entityflowrecord.data.model.LightInfoItem
import com.setruth.entityflowrecord.data.model.LightRange
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.data.model.TipLight
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import com.setruth.entityflowrecord.ui.pages.setting.SettingViewModel
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplicationPreview
import org.koin.dsl.module


@Composable
fun ThresholdConfigCard(
    modifier: Modifier = Modifier,
    lightRange: LightRange,
    onRangeUpdate:(LightRange)-> Unit={},
) {
    val lightList = listOf(
        LightInfoItem(TipLight.GREEN, "绿灯状态", "正常通行，允许进入"),
        LightInfoItem(TipLight.YELLOW, "黄灯状态", "谨慎通行，注意人数"),
        LightInfoItem(TipLight.RED, "红灯状态", "禁止进入，已达上限")
    )
    var updateDialogShow by remember { mutableStateOf(false) }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "灯光提示",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    modifier = Modifier
                        .defaultMinSize(0.dp)
                        .padding(0.dp)
                        .height(30.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = { updateDialogShow = true },
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 3.dp, horizontal = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        text = "更新范围",
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            lightList.forEachIndexed { index, item ->
                ThresholdItemRow(item = item, lightRange)
                if (index < lightList.lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp)) // 行间距
                }
            }
        }
    }
    if (updateDialogShow) {
        UpdateLightRangeDialog(
            lightRange,
            onCancel = { updateDialogShow = false },
            onConfirm = {
                updateDialogShow = false
                onRangeUpdate(it)
            }
        )

    }
}

@Composable
fun ThresholdItemRow(item: LightInfoItem, lightRange: LightRange) {

    val indicatorColor = when (item.status) {
        TipLight.GREEN -> Color(0xFF66BB6A) // 绿色
        TipLight.YELLOW -> Color(0xFFFFCA28) // 黄色
        TipLight.RED -> Color(0xFFEF5350) // 红色
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
            val rangeText = when (item.status) {
                TipLight.GREEN -> "小于${lightRange.warn}人"
                TipLight.YELLOW -> "${lightRange.warn}~${lightRange.err}人"
                TipLight.RED -> "大于${lightRange.err}人"
            }
            Text(
                text = rangeText,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun ThresholdConfigCardPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        KoinApplicationPreview(application = {
            modules(appModule, module {
                single { BluetoothRepository() }
            })
        }) {
            Box(Modifier.padding(10.dp)) {
                ThresholdConfigCard(lightRange = LightRange())
            }
        }
    }
}