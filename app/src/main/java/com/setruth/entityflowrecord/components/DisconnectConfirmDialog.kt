package com.setruth.entityflowrecord.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@SuppressLint("MissingPermission")
@Composable
fun DisconnectConfirmDialog(
    device: BluetoothDevice? = null,
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    if (device == null) {
        return
    }
    AlertDialog(
        title = {
            Text(text = "是否断开连接")
        },
        text = {
            Text(text = "当前连接的设备[${device.name ?: device.address}]将断开连接，将无法收到设备消息和进行设备控制！")
        },
        onDismissRequest = {
            onCancel()
        },
        icon = {
            Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Example Icon")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
                }
            ) {
                Text("取消")
            }
        }
    )
}