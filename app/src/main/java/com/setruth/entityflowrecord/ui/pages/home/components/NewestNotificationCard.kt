package com.setruth.entityflowrecord.ui.pages.home.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setruth.entityflowrecord.components.FlowNotificationItem
import com.setruth.entityflowrecord.model.FLowType
import com.setruth.entityflowrecord.model.FlowBaseRecord
import com.setruth.entityflowrecord.model.ThemeMode
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewestNotificationCard() {
    var countNum = 100
    val notifications: List<FlowBaseRecord> = remember {
        (1..10).map { i ->
            val timestamp = LocalDateTime.now()

            val isEntry = Random.nextBoolean()
            val type = if (isEntry) FLowType.ENTRY else FLowType.EXIT
            val currentTotal = countNum + if (isEntry) (1) else (-1)
            countNum = currentTotal
            object : FlowBaseRecord {
                override val timestamp: LocalDateTime
                    get() = timestamp
                override val type: FLowType
                    get() = type
                override val changeAfterTotalPeople: Int
                    get() = currentTotal

            }
        }
    }
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                text = "最近10条进出通知",
            )
            Spacer(modifier = Modifier.height(5.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications.withIndex().toList()) { (index, record) -> // 解构 IndexedValue
                    FlowNotificationItem(index = index + 1, record = record)
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun NewestNotificationCardPreview() {
    EntityFlowRecordTheme(darkTheme = ThemeMode.LIGHT) {
        Box(Modifier.padding(10.dp)) {
            NewestNotificationCard()
        }
    }
}