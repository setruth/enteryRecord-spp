package com.setruth.entityflowrecord.ui.pages.devices.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.BOND_BONDED
import android.bluetooth.BluetoothDevice.BOND_BONDING
import android.bluetooth.BluetoothDevice.BOND_NONE
import android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC
import android.bluetooth.BluetoothDevice.DEVICE_TYPE_DUAL
import android.bluetooth.BluetoothDevice.DEVICE_TYPE_LE
import android.bluetooth.BluetoothDevice.DEVICE_TYPE_UNKNOWN
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme
import com.setruth.entityflowrecord.util.getIconForMajorDeviceClass

@SuppressLint("MissingPermission")
@Composable
fun ConnectDialog(
    device: BluetoothDevice? = null,
    isConnect: Boolean=false,
    cancel: () -> Unit = {},
    cancelBound: (BluetoothDevice) -> Unit = {},
    disConnect: (BluetoothDevice?) -> Unit = {},
    connect: (BluetoothDevice?) -> Unit = {},
) {
    Dialog(onDismissRequest = { cancel() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getIconForMajorDeviceClass(device?.bluetoothClass!!.majorDeviceClass),
                        contentDescription = null,
                        modifier = Modifier.size(27.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "设备${device.name ?: "(未知设备)"}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "设备名称：${device?.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "设备地址：${device?.address}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    val bondState = when (device?.bondState) {
                        BOND_NONE -> "未绑定过"
                        BOND_BONDING -> "正在绑定"
                        BOND_BONDED -> "绑定过"
                        else -> "未知"
                    }
                    Text(text = "设备状态：$bondState", style = MaterialTheme.typography.bodyMedium)
                    val typeName = when (device?.type) {
                        DEVICE_TYPE_UNKNOWN -> "未知设备类型"
                        DEVICE_TYPE_CLASSIC -> "经典蓝牙 (BR/EDR)"
                        DEVICE_TYPE_LE -> "低功耗蓝牙 (LE)"
                        DEVICE_TYPE_DUAL -> "双模 (BR/EDR/LE)"
                        else -> "无效设备类型"
                    }
                    Text(text = "蓝牙类型：$typeName", style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(
                        onClick = cancel,
                    ) {
                        Text("取消")
                    }
                    Row {
                        if (device?.bondState == BOND_BONDED) {
                            TextButton(
                                onClick = { cancelBound(device) },
                            ) {
                                Text("取消配对")
                            }
                        }

                        if (isConnect){
                            TextButton(
                                onClick = { disConnect(device) },
                            ) {
                                Text("断开连接")
                            }
                        }else{
                            TextButton(
                                onClick = { connect(device) },
                            ) {
                                Text("连接")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun ConnectDialogPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            ConnectDialog()
        }
    }
}