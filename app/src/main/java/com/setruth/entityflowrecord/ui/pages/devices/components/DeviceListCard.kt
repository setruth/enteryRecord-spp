package com.setruth.entityflowrecord.ui.pages.devices.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothClass.Device
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.ui.pages.devices.DevicesView
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme
import com.setruth.entityflowrecord.util.getIconForMajorDeviceClass


@SuppressLint("MissingPermission")
@Composable
fun DeviceListCard(
    modifier: Modifier = Modifier,
    title: String = "设备列表",
    isLoading: Boolean = false,
    enableUnknownFilter: Boolean = false,
    devices: List<BluetoothDevice> = emptyList(),
    deviceConnect: (BluetoothDevice) -> Unit = {}
) {
    var showUnknownDevice by remember { mutableStateOf(false) }
    val filterText = if (showUnknownDevice) {
        "隐藏未知设备"
    } else {
        "显示未知设备"
    }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    text = title,
                )
                if (enableUnknownFilter) {
                    TextButton(
                        modifier = Modifier
                            .defaultMinSize(0.dp)
                            .padding(0.dp)
                            .height(30.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = { showUnknownDevice = !showUnknownDevice },
                    ) {
                        Text(
                            modifier = Modifier.padding(vertical = 3.dp, horizontal = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            text = filterText,
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.height(5.dp))
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (isLoading) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "正在扫描",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    return@Column
                }
                val renderList = if (showUnknownDevice) {
                    devices
                } else {
                    devices.filter { it.name != null }
                }
                if (renderList.isEmpty()) {
                    Text(
                        text = "暂无设备",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(renderList.withIndex().toList()) { (index, device) ->
                            DeviceListItem(device) {
                                deviceConnect(device)
                            }
                            if (index < devices.lastIndex) {
                                HorizontalDivider(modifier = modifier.padding(horizontal = 10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}


@SuppressLint("MissingPermission", "NewApi")
@Composable
private fun DeviceListItem(
    device: BluetoothDevice,
    connectAddress: String = "",
    onConnectClick: (BluetoothDevice) -> Unit
) = Column(
    modifier = Modifier.padding(horizontal = 16.dp)
) {
    Spacer(modifier = Modifier.height(5.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getIconForMajorDeviceClass(device.bluetoothClass.majorDeviceClass),
                contentDescription = null,
                modifier = Modifier.size(27.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(5.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name ?: "(未知设备)",
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (connectAddress == device.address) {
                    Text(
                        text = "已连接",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    val boundStr = if (device.bondState == BluetoothDevice.BOND_NONE) {
                        "未绑定"
                    } else if (device.bondState == BluetoothDevice.BOND_BONDED) {
                        "已绑定"
                    }else{
                        "正在绑定"
                    }
                    Text(
                        text = boundStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onConnectClick(device) },
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null
        )
    }
    Spacer(modifier = Modifier.height(5.dp))
}


@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun DeviceListCardPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            DeviceListCard(isLoading = true, enableUnknownFilter = true)
        }
    }
}