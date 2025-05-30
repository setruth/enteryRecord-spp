package com.setruth.entityflowrecord.data.repository

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothRepository(private val bluetoothAdapter: BluetoothAdapter) {

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _connectedDevice = MutableStateFlow<BluetoothDevice?>(null)
    val connectedDevice: StateFlow<BluetoothDevice?> = _connectedDevice.asStateFlow()

    fun connectToDevice(device: BluetoothDevice) {
        _isConnected.value = true
        _connectedDevice.value = device
    }

    fun disconnect() {
        _isConnected.value = false
        _connectedDevice.value = null
        println("Bluetooth: Disconnected")
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun getBondedDevices(): Set<BluetoothDevice> {
        return bluetoothAdapter.bondedDevices ?: emptySet()
    }
}