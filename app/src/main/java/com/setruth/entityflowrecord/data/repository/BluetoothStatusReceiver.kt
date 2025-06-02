package com.setruth.entityflowrecord.data.repository

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import com.setruth.entityflowrecord.data.model.BluetoothStatusListener

/**
 * 这是一个监听蓝牙扫描、连接和配对状态的广播接收器。
 * 可以通过回调函数通知外部状态变化。
 */
class BluetoothStatusReceiver(
    private val context: Context,
    private val statusCallback: BluetoothStatusListener
) : BroadcastReceiver() {
    companion object {
        private const val TAG = "BluetoothStatusReceiver"
    }

    init {
        unregister()
        context.registerReceiver(this, IntentFilter().apply {
            // 扫描相关的 Action
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_FOUND)

            // 连接相关的 Action
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED) // 蓝牙连接状态变化，API 18+

            // 配对相关的 Action
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED) // 配对状态变化
        })
    }

    fun unregister() {
        try {
            context.unregisterReceiver(this)
            Log.d(TAG, "蓝牙广播接收器注销注册")
        } catch (e: IllegalArgumentException) {
            // 如果接收器已经未注册，这里会抛出异常
            Log.e(TAG, "注销了一个未注册的广播接收器: ${e.message}")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }

        when (action) {
            // --- 蓝牙扫描相关 ---
            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                Log.d(TAG, "蓝牙扫描开始")
                statusCallback.onDiscoveryStarted()
            }

            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                Log.d(TAG, "蓝牙扫描结束")
                statusCallback.onDiscoveryFinished()
            }

            BluetoothDevice.ACTION_FOUND -> {
                device?.let { newDevice ->
                    statusCallback.onDeviceFound(newDevice)
                }
            }

            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                Log.d(TAG, "蓝牙设备连接成功: ${device?.name ?: device?.address}")
            }

            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                Log.d(TAG, "蓝牙设备断开连接: ${device?.name ?: device?.address}")
                statusCallback.onDisconnected(device)
            }

            BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> {
                val state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_CONNECTION_STATE,
                    BluetoothAdapter.ERROR
                )
                when (state) {
                    BluetoothAdapter.STATE_CONNECTED -> {
                        Log.d(TAG, "蓝牙连接状态变化->连接成功: ${device?.name ?: device?.address}")
                    }

                    BluetoothAdapter.STATE_DISCONNECTED -> {
                        Log.d(
                            TAG,
                            "蓝牙连接状态变化->断开连接: ${device?.name ?: device?.address}"
                        )
                        statusCallback.onDisconnected(device)
                    }
                }
            }

            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                val bondState =
                    intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
                val previousBondState = intent.getIntExtra(
                    BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
                    BluetoothDevice.ERROR
                )

                Log.d(
                    TAG,
                    "绑定的设备状态变化 ${device?.name ?: device?.address}: $previousBondState -> $bondState"
                )

                when (bondState) {
                    BluetoothDevice.BOND_BONDED -> {
                        Log.d(TAG, "蓝牙已经绑定上: ${device?.name ?: device?.address}")
                        statusCallback.onBoundedState(device)
                    }

                    BluetoothDevice.BOND_BONDING -> {
                        Log.d(TAG, "蓝牙正在绑定: ${device?.name ?: device?.address}")
                    }

                    BluetoothDevice.BOND_NONE -> {
                        Log.d(TAG, "蓝牙未绑定: ${device?.name ?: device?.address}")
                    }
                }

            }
        }
    }

}