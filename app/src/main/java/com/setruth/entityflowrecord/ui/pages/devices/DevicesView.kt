package com.setruth.entityflowrecord.ui.pages.devices

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.BluetoothConnected
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.setruth.entityflowrecord.data.di.appModule
import com.setruth.entityflowrecord.data.model.ERROR_COLOR
import com.setruth.entityflowrecord.data.model.SUCCESS_COLOR
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.data.repository.BluetoothConnectionState
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import com.setruth.entityflowrecord.ui.frame.MainFrame
import com.setruth.entityflowrecord.ui.pages.devices.components.ConnectDialog
import com.setruth.entityflowrecord.ui.pages.devices.components.DeviceListCard
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplicationPreview
import org.koin.dsl.module


@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesView(viewModel: DevicesViewModel = koinViewModel(), onFinish: () -> Unit = {}) {
    val connectDevice by viewModel.connectedDevice.collectAsState()
    val discoveredDevices by viewModel.discoveredDevices.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val scanLoading by viewModel.scanLoading.collectAsState()
    var loadingConnectDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    var disconnectConfirmDialogShow by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.startBluetoothScan()
    }
    LaunchedEffect(connectionState) {
        when (connectionState) {
            is BluetoothConnectionState.Connecting -> {
                Toast.makeText(context, "正在连接", Toast.LENGTH_SHORT).show()
            }
            is BluetoothConnectionState.Connected -> {
                loadingConnectDevice = null
                Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show()
            }

            is BluetoothConnectionState.ConnectionFailed -> {
                Toast.makeText(
                    context,
                    "${(connectionState as BluetoothConnectionState.ConnectionFailed).errorMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            BluetoothConnectionState.None -> {}
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("蓝牙设备连接") },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(
                        enabled = connectionState != BluetoothConnectionState.Connecting,
                        onClick = {
                            viewModel.startBluetoothScan()
                        }) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "update")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 15.dp)
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnimatedVisibility(
                visible = connectDevice != null,
                enter = expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                val connectDeviceName = connectDevice?.let {
                    "当前连接：${it.name ?: it.address}"
                } ?: "(暂无设备连接)"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.BluetoothConnected, contentDescription = null)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = connectDeviceName)
                    }
                    if (connectDevice != null) {
                        TextButton(
                            modifier = Modifier
                                .defaultMinSize(0.dp)
                                .padding(0.dp)
                                .height(30.dp),
                            contentPadding = PaddingValues(0.dp),
                            onClick = {
                                disconnectConfirmDialogShow = true
                            },
                        ) {
                            Text(
                                modifier = Modifier.padding(vertical = 3.dp, horizontal = 8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                text = "断开连接",
                            )
                        }
                    }
                }
            }
            if (connectionState == BluetoothConnectionState.Connecting) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "设备正在连接",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                return@Column
            }
            DeviceListCard(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                title = "附近设备",
                devices = discoveredDevices,
                isLoading = scanLoading,
                enableUnknownFilter = true
            ) { loadingConnectDevice = it }
        }
    }
    if (loadingConnectDevice != null) {
        ConnectDialog(
            device = loadingConnectDevice,
            cancel = {
                loadingConnectDevice = null
            },
            cancelBound = {
                viewModel.cancelDeviceBound(it)
                loadingConnectDevice = null
            },
            disConnect = {
                viewModel.disconnect()
                loadingConnectDevice = null
            },
            connect = {
                if (it != null) {
                    viewModel.connectDevice(it)
                }
                loadingConnectDevice = null
            }
        )
    }
    if (disconnectConfirmDialogShow) {
        AlertDialog(
            title = {
                Text(text = "是否断开连接")
            },
            text = {
                Text(text = "当前连接的设备[${connectDevice?.name ?: connectDevice?.address}]将断开连接，将无法收到设备消息和进行设备控制！")
            },
            onDismissRequest = {
                disconnectConfirmDialogShow = false

            },
            icon = {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Example Icon")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.disconnect()
                        disconnectConfirmDialogShow = false
                    }
                ) {
                    Text("确认")

                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        disconnectConfirmDialogShow = false
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun DevicesViewPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        KoinApplicationPreview(application = {
            modules(appModule, module {
                single { BluetoothRepository() }
            })
        }) {
            DevicesView()
        }
    }
}