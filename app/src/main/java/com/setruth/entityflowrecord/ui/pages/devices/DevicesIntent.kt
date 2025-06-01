package com.setruth.entityflowrecord.ui.pages.devices

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.setruth.entityflowrecord.data.repository.BluetoothConnectionState
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class DevicesViewModel(
    private val bluetoothRepository: BluetoothRepository
) : ViewModel() {
    companion object {
        const val TAG = "DevicesViewModel"
    }

    private val _scanLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val scanLoading: StateFlow<Boolean> = _scanLoading
    val connectionState: StateFlow<BluetoothConnectionState> = bluetoothRepository.connectionState
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = bluetoothRepository.discoveredDevices
    val connectedDevice: StateFlow<BluetoothDevice?> = bluetoothRepository.connectedDevice
    fun startBluetoothScan() {
        bluetoothRepository.startDiscoveryAutoCancel()
        viewModelScope.launch {
            _scanLoading.emit(true)
            delay(2000)
            _scanLoading.emit(false)
        }
    }

    fun connectDevice(device: BluetoothDevice) {
        bluetoothRepository.connectDevice(device)
    }


    fun cancelDeviceBound(device: BluetoothDevice) {
        bluetoothRepository.cancelDeviceBound(device)
    }

    fun disconnect() {
        bluetoothRepository.disconnect()
    }


}