package com.setruth.entityflowrecord.data.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.BOND_BONDED
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import com.setruth.entityflowrecord.data.model.BluetoothStatusListener
import com.setruth.entityflowrecord.data.model.CMD_CONNECT
import com.setruth.entityflowrecord.data.model.CMD_DISCONNECT
import com.setruth.entityflowrecord.data.model.SPP_UUID
import com.setruth.entityflowrecord.util.CommunicationThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@SuppressLint("MissingPermission")
class BluetoothRepository(
    private val context: Context? = null,
    private val bluetoothAdapter: BluetoothAdapter? = null,
) {
    private val TAG = "BluetoothRepository"
    private val bluetoothStatusReceiver: BluetoothStatusReceiver?
    private val _connectedDevice = MutableStateFlow<BluetoothDevice?>(null)
    private var _connectionState =
        MutableStateFlow<BluetoothConnectionState>(BluetoothConnectionState.None)
    val connectionState = _connectionState.asStateFlow()
    private var bluetoothScanCancelTimerJob: Job? = null //扫描取消定时器任务
    val connectedDevice: StateFlow<BluetoothDevice?> = _connectedDevice.asStateFlow()
    private var loadingDevice: BluetoothDevice? = null
    private var _bluetoothSocket: BluetoothSocket? = null
    private val _ioStream = MutableStateFlow<BluetoothIoStream>(BluetoothIoStream.Disconnected)
    val ioStream: StateFlow<BluetoothIoStream> = _ioStream.asStateFlow()
    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices.asStateFlow()
    private val bluetoothStatusCallback = object : BluetoothStatusListener {
        override fun onDeviceFound(device: BluetoothDevice) {
            _discoveredDevices.update { currentDevices ->
                val devicesWithoutDuplicate = currentDevices.filter { it.address != device.address }

                val updatedList = (devicesWithoutDuplicate + device).toMutableList()

                updatedList.sortWith(Comparator { device1, device2 ->
                    val isDevice1Bonded = (device1.bondState == BOND_BONDED)
                    val isDevice2Bonded = (device2.bondState == BOND_BONDED)
                    when {
                        isDevice1Bonded && !isDevice2Bonded -> -1
                        !isDevice1Bonded && isDevice2Bonded -> 1
                        else -> 0
                    }
                })
                updatedList
            }
        }

        override fun onDisconnected(device: BluetoothDevice?) {
            if (connectedDevice.value?.address == device?.address) {
                startDiscoveryAutoCancel()
                release()
            }
        }

        override fun onBoundedState(device: BluetoothDevice?) {
            if (loadingDevice?.address == device?.address) {
                connectDevice(device!!)
                loadingDevice = null
            }
        }

    }

    init {
        bluetoothStatusReceiver =
            context?.let { BluetoothStatusReceiver(it, bluetoothStatusCallback) }
    }


    /**
     * 开始扫描附近的蓝牙设备。
     *
     *
     * 扫描会清空当前扫描列表，并会使用startDiscovery进行扫描，并且扫描前会关闭之前的扫描，
     * 如果有扫描结束的定时任务也会直接cancel掉那个job
     *
     * **注意：此函数需要 BLUETOOTH_SCAN 权限。请确保在调用前已获取。**
     *
     * @param second 用于设定多少秒后自动关闭扫描。
     * - **默认值 5 秒。**
     * - 传入 `null` 值表示不手动管理扫描结束时间（扫描将持续到系统默认超时或被手动取消）。
     * - `startDiscovery` 虽然会自动关闭，但其时间无法精确控制，且可能因运行环境而异。
     *
     * @throws IllegalArgumentException 如果 `second` 参数传入非空但小于等于 `0` 的值，则抛出此异常。
     *
     */
    fun startDiscoveryAutoCancel(second: Int? = 5) {
        bluetoothScanCancelTimerJob?.also {
            it.cancel()
            bluetoothScanCancelTimerJob = null
        }
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
        if (second != null) {
            require(second > 0) { "扫描取消时间不能小于0" }
            bluetoothScanCancelTimerJob = CoroutineScope(Dispatchers.IO).launch {
                delay(second.seconds)
                if (bluetoothAdapter?.isDiscovering == true) {
                    bluetoothAdapter.cancelDiscovery()
                }
            }
        }
        _discoveredDevices.value = emptyList()
        bluetoothAdapter?.startDiscovery()
    }


    /**
     * 尝试连接指定的蓝牙设备。
     *
     * 在尝试连接之前，会检查当前是否已经有已连接的设备。如果有，则不会执行连接操作并返回 false。
     *
     * @param device 要连接的蓝牙设备对象（BluetoothDevice）。
     * @return 如果成功启动连接流程或设备已经配对，则返回 true；如果已有连接或出现异常，则返回 false。
     *
     * **行为说明：**
     * - 如果设备未配对（BOND_NONE），将尝试创建配对，并设置该设备为加载中的设备。
     * - 如果设备已配对（BOND_BONDED），则直接开始建立连接。
     */
    fun connectDevice(device: BluetoothDevice): Boolean {
        _connectionState.value = BluetoothConnectionState.Connecting
        if (connectedDevice.value != null) {
            return false
        }
        when (device.bondState) {
            BluetoothDevice.BOND_NONE -> {
                device.createBond()
                loadingDevice = device
            }

            BOND_BONDED -> createConnect(device)
        }
        return true
    }

    private fun createConnect(device: BluetoothDevice) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _bluetoothSocket =
                    device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID))
            } catch (e: IOException) {
                _connectionState.value = BluetoothConnectionState.ConnectionFailed("蓝牙连接错误")
                Log.e(TAG, "蓝牙连接错误", e)
                _connectedDevice.value = null
                return@launch
            } catch (e: SecurityException) {
                _connectionState.value = BluetoothConnectionState.ConnectionFailed("蓝牙权限出错")
                Log.e(TAG, "蓝牙权限出错", e)
                _connectedDevice.value = null
                return@launch
            }
            try {
                _bluetoothSocket?.connect()
                _connectedDevice.value = device
                _bluetoothSocket?.let { socket ->
                    _connectionState.value = BluetoothConnectionState.Connected
                    val inputStream = socket.inputStream
                    val outputStream = socket.outputStream
                    startDiscoveryAutoCancel() //连接成功后再次扫描蓝牙刷新周围设备列表
                    _ioStream.value = BluetoothIoStream.Connected(inputStream, outputStream)
                    val communicationThread = CommunicationThread(
                        inputStream,
                        outputStream,
                        {
                            Log.e(
                                TAG,
                                "通信线程出错 ，已断开连接"
                            )
                            disconnectAfter()
                        },
                        { receivedBytes, bytesRead ->
                            val hexString = receivedBytes.sliceArray(0 until bytesRead)
                                .joinToString(" ") { "%02X".format(it) }
                            Log.d(TAG, "Android 收到原始十六进制数据 ($bytesRead 字节): $hexString")
                            try {
                                val data = String(receivedBytes, 0, bytesRead, Charsets.US_ASCII)
                                Log.d(TAG, "读取到蓝牙给的数据 (String - ASCII): $data")
                            } catch (e: Exception) {
                                Log.e(TAG, "ASCII 解码失败: ${e.message}", e)
                            }
                            try {
                                val dataUtf8 = String(receivedBytes, 0, bytesRead, Charsets.UTF_8)
                                Log.d(TAG, "读取到蓝牙给的数据 (String - UTF-8): $dataUtf8")
                            } catch (e: Exception) {
                                Log.e(TAG, "UTF-8 解码失败: ${e.message}", e)
                            }

                        }
                    )
                    communicationThread.start()
                    val command = CMD_CONNECT
                    Log.d(TAG, "createConnect: 发送指令${command}")
                    communicationThread.write("*setruth~".toByteArray())
                }
            } catch (connectException: IOException) {
                val baseTip = "无法连接远程蓝牙设备"
                _connectionState.value = BluetoothConnectionState.ConnectionFailed(baseTip)
                Log.e(TAG, baseTip, connectException)
                try {
                    _bluetoothSocket?.close()
                } catch (closeException: IOException) {
                    Log.e(
                        TAG,
                        "无法关闭远程蓝牙Socket",
                        closeException
                    )
                } finally {
                    _connectedDevice.value = null
                    _ioStream.value = BluetoothIoStream.Disconnected
                }
            } catch (securityException: SecurityException) {
                val baseTip = "权限不够无法打开远程蓝牙连接"
                _connectionState.value = BluetoothConnectionState.ConnectionFailed(baseTip)
                Log.e(TAG, baseTip, securityException)
                try {
                    _bluetoothSocket?.close()
                } catch (closeException: IOException) {
                    Log.e(TAG, "无法关闭远程蓝牙Socket", closeException)
                } finally {
                    _connectedDevice.value = null
                    _ioStream.value = BluetoothIoStream.Disconnected
                }
            }
        }

    /**
     * 释放所有资源 断开连接
     */
    private fun release() {
        try {
            _bluetoothSocket?.outputStream?.close()
            _bluetoothSocket?.inputStream?.close()
            _bluetoothSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "蓝牙无法关闭", e)
        } finally {
            _bluetoothSocket = null
            _connectedDevice.value = null
            _ioStream.value = BluetoothIoStream.Disconnected
        }
    }

    /**
     * 取消连接的后续。
     * 取消连接之后的操作。
     */
    private fun disconnectAfter() {
        release()
        startDiscoveryAutoCancel()
    }

    fun disconnect() {
        when (ioStream.value) {
            is BluetoothIoStream.Connected -> {
                (ioStream.value as BluetoothIoStream.Connected).outputStream.write(CMD_DISCONNECT.toByteArray())
            }

            BluetoothIoStream.Disconnected -> {}
        }
        disconnectAfter()
    }

    fun cancelDeviceBound(device: BluetoothDevice) {
        device.javaClass.getMethod("removeBond").invoke(device)
        startDiscoveryAutoCancel()
    }

    /**
     * 注销蓝牙线管的广播接收器，需要手动注销，避免内存泄露
     */
    fun unregister() {
        bluetoothStatusReceiver?.unregister()
    }

    /**
     * 重置当前设备的连接状态
     */
    fun resetConnectionState() {
        _connectionState.value = BluetoothConnectionState.None
    }
}

sealed class BluetoothIoStream {
    data class Connected(val inputStream: InputStream, val outputStream: OutputStream) :
        BluetoothIoStream()

    data object Disconnected : BluetoothIoStream()
}

sealed class BluetoothConnectionState {
    /**
     * 没有设备连接。
     */
    object None : BluetoothConnectionState()

    /**
     * 正在尝试连接到设备。
     */
    object Connecting : BluetoothConnectionState()

    /**
     * 已成功连接到设备。
     */
    object Connected : BluetoothConnectionState()

    /**
     * 连接尝试失败。
     * @param errorMessage 失败的原因信息，可选。
     */
    data class ConnectionFailed(val errorMessage: String? = null) : BluetoothConnectionState()
}