package com.setruth.entityflowrecord.ui.pages.notification.components

import android.widget.DatePicker
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
    datePickerState: DatePickerState,
    show:Boolean,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!show) return
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }
    DatePickerDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {

                },
                enabled = confirmEnabled.value
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) { Text("取消") }
        }
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        )
    }
}