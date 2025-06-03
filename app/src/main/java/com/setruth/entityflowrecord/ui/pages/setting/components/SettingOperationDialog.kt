package com.setruth.entityflowrecord.ui.pages.setting.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.setruth.entityflowrecord.data.model.LightRange
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme

/**
 * 更新灯的显示范围的弹窗
 */
@Composable
fun UpdateLightRangeDialog(
    lightRange: LightRange,
    onCancel: () -> Unit = {},
    onConfirm: (LightRange) -> Unit = {},
) {
    var warnRange by remember { mutableStateOf(lightRange.warn.toString()) }
    var errRange by remember { mutableStateOf(lightRange.err.toString()) }
    val context = LocalContext.current
    Dialog(onDismissRequest = { onCancel }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(27.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "灯光范围更新",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    IntValueInput(
                        warnRange,
                        onValueUpdate = {
                            warnRange = it
                        },
                        label = "大于多少黄灯亮"
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    IntValueInput(
                        errRange,
                        onValueUpdate = {
                            errRange = it
                        },
                        label = "大于多少红灯亮"
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(
                        onClick = onCancel,
                    ) {
                        Text("取消")
                    }
                    Row {
                        TextButton(
                            onClick = {
                                if (!checkInput(warnRange)) {
                                    Toast.makeText(context, "黄灯范围输入有误", Toast.LENGTH_SHORT)
                                        .show()
                                    return@TextButton
                                }
                                if (!checkInput(errRange)) {
                                    Toast.makeText(context, "红灯范围输入有误", Toast.LENGTH_SHORT)
                                        .show()
                                    return@TextButton
                                }
                                onConfirm(
                                    LightRange(
                                        warnRange.toInt(),
                                        errRange.toInt(),
                                    )
                                )
                            },
                        ) {
                            Text("确定")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 更新容量总数的弹窗
 */
@Composable
fun UpdateFullCardDialog(
    count: Int,
    onCancel: () -> Unit = {},
    onConfirm: (Int) -> Unit = {},
) {
    var countValue by remember { mutableStateOf(count.toString()) }
    val context = LocalContext.current
    Dialog(onDismissRequest = { onCancel }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(27.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "更新最大容量",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                IntValueInput(
                    countValue,
                    onValueUpdate = {
                        countValue = it
                    },
                    label = "最大人数为多少"
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(
                        onClick = onCancel,
                    ) {
                        Text("取消")
                    }
                    Row {
                        TextButton(
                            onClick = {
                                if (!checkInput(countValue)) {
                                    Toast.makeText(context, "输入的总数有误", Toast.LENGTH_SHORT)
                                        .show()
                                    return@TextButton
                                }
                                onConfirm(countValue.toInt())
                            },
                        ) {
                            Text("确定")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 大于0的整数输入框
 */
@Composable
private fun IntValueInput(
    value: String,
    label: String,
    onValueUpdate: (String) -> Unit,
) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = {
            onValueUpdate(it)
        },
        supportingText = {
            if (value.toIntOrNull() != null) {
                if (value.toInt() < 0) {
                    Text(text = "请输入大于0的数字")
                } else {
                    return@OutlinedTextField
                }
            } else {
                Text(text = "请输入数字")
            }

        },
        label = { Text(text = label) },
        isError = !checkInput(value),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * 用于检测是否是一个合法的大于等于0的值
 */
private fun checkInput(value: String): Boolean = if (value.toIntOrNull() != null) {
    value.toInt() >= 0
} else false

@Preview(showBackground = true)
@Composable
fun UpdateLightRangeDialogPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            UpdateLightRangeDialog(
                LightRange()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateFullCardDialogPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            UpdateFullCardDialog(
                10
            )
        }
    }
}
