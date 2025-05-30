package com.setruth.entityflowrecord.ui.pages.notification

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.setruth.entityflowrecord.components.FlowNotificationItem
import com.setruth.entityflowrecord.data.model.FLowType
import com.setruth.entityflowrecord.data.model.FlowBaseRecord
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.ui.frame.MainFrame
import com.setruth.entityflowrecord.ui.pages.notification.components.DateDialog
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale
import kotlin.random.Random


enum class FilterMode {
    ALL,
    ENTRY,
    EXIT
}

data class FilterData(
    val name: String,
    val mode: FilterMode
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationView() {
    val nowTimestamp = System.currentTimeMillis()
    val context = LocalContext.current
    var showFilterOptions by remember { mutableStateOf(false) }
    val filterOptions = remember {
        listOf(
            FilterData("全部", FilterMode.ALL),
            FilterData("进店", FilterMode.ENTRY),
            FilterData("离店", FilterMode.EXIT)
        )
    }
    var filterMode by remember { mutableStateOf(filterOptions[0]) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = nowTimestamp
    )
    var countNum = 100
    val listSize = 100
    val notifications: List<FlowBaseRecord> = remember {
        (1..listSize).map { i ->
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
    var renderList by remember { mutableStateOf<List<FlowBaseRecord>>(emptyList()) }
    LaunchedEffect(filterMode) {
        renderList = when (filterMode.mode) {
            FilterMode.ALL -> {
                notifications
            }

            FilterMode.ENTRY -> {
                notifications.filter { it.type == FLowType.ENTRY }
            }

            FilterMode.EXIT -> {
                notifications.filter { it.type == FLowType.EXIT }
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        DatePickerDocked(datePickerState) {

        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp), // 固定内边距
            ) {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    text = "高峰期人数分析",
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "最高人数",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "35人(12:11:07)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "最低人数",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "12人(17:06:11)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Text(
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                text = "共${renderList.size}条",
                color = MaterialTheme.colorScheme.tertiary
            )
            Row(
                modifier = Modifier.clickable { showFilterOptions = true },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                    text = filterMode.name,
                )
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
                DropdownMenu(
                    expanded = showFilterOptions,
                    onDismissRequest = { showFilterOptions = false }
                ) {
                    filterOptions.forEach {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    style = MaterialTheme.typography.bodyMedium,
                                    text = it.name,
                                    color = if (it.mode == filterMode.mode) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                filterMode = it
                                showFilterOptions = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(renderList.withIndex().toList()) { (index, record) -> // 解构 IndexedValue
                FlowNotificationItem(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ), index = index + 1, record = record
                )
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDocked(datePickerState: DatePickerState, onDateChange: () -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            label = { Text("查询日期") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = "选择日期"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        DateDialog(
            datePickerState,
            showDatePicker,
            onDateSelected = {
                onDateChange()
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }
}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun HomeViewPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        MainFrame(1)
    }
}