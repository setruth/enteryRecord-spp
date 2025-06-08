package com.setruth.entityflowrecord.ui.pages.devices

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.setruth.entityflowrecord.data.repository.BluetoothConnectionState
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import com.setruth.entityflowrecord.data.repository.CMDBTRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class DevicesViewModel(
    private val cmdBTRepository: CMDBTRepository
) : ViewModel() {
    companion object {
        const val TAG = "DevicesViewModel"
    }

    private val _scanLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val scanLoading: StateFlow<Boolean> = _scanLoading
    val connectionState: StateFlow<BluetoothConnectionState> = cmdBTRepository.connectionState
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = cmdBTRepository.discoveredDevices
    val connectedDevice: StateFlow<BluetoothDevice?> = cmdBTRepository.connectedDevice
    fun startBluetoothScan() {
        cmdBTRepository.startDiscoveryAutoCancel()
        viewModelScope.launch {
            _scanLoading.emit(true)
            delay(2000)
            _scanLoading.emit(false)
        }
    }

    fun connectDevice(device: BluetoothDevice) {
        cmdBTRepository.connectDevice(device)
    }


    fun cancelDeviceBound(device: BluetoothDevice) {
        cmdBTRepository.cancelDeviceBound(device)
    }

    fun disconnect() {
        if (cmdBTRepository.sendDisconnectCommand()) {
            cmdBTRepository.disconnect()
        }
    }

    fun resetConnectionState() {
        cmdBTRepository.resetConnectionState()
    }

}