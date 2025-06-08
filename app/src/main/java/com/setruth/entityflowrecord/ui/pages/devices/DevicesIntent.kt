package com.setruth.entityflowrecord.ui.pages.devices

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.setruth.entityflowrecord.data.model.ConfigKeys
import com.setruth.entityflowrecord.data.model.DEFAULT_ALARM_LIGHT
import com.setruth.entityflowrecord.data.model.DEFAULT_BUZZ_ON
import com.setruth.entityflowrecord.data.model.DEFAULT_ERR_LIGHT
import com.setruth.entityflowrecord.data.model.DEFAULT_FULL_STOP_ON
import com.setruth.entityflowrecord.data.model.DEFAULT_MAX_COUNT
import com.setruth.entityflowrecord.data.repository.BluetoothConnectionState
import com.setruth.entityflowrecord.data.repository.CMDBTRepository
import com.tencent.mmkv.MMKV
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
    val initConfigState = cmdBTRepository.initConfigState

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

    fun uploadConfig() {
        MMKV.defaultMMKV().apply {
            val maxCount = decodeInt(ConfigKeys.FULL_COUNT, DEFAULT_MAX_COUNT)
            val currentCount = 0
            val buzzOn =
                if (decodeBool(ConfigKeys.VOICE_REMIND_ENABLE, DEFAULT_BUZZ_ON)) 1 else 0
            val fullStopOn =
                if (decodeBool(ConfigKeys.NO_ENTRANCE_ENABLE, DEFAULT_FULL_STOP_ON)) 1 else 0
            val alarmLight = decodeInt(ConfigKeys.YELLOW_LIGHT_RANGE, DEFAULT_ALARM_LIGHT)
            val errLight = decodeInt(ConfigKeys.RED_LIGHT_RANGE, DEFAULT_ERR_LIGHT)
            cmdBTRepository.sendSTMInit(
                maxCount = maxCount,
                currentCount = currentCount,
                fullStopOn = fullStopOn,
                buzzOn = buzzOn,
                alarmLight = alarmLight,
                errLight = errLight
            )

        }

    }

    fun restInitConfigState() {
        cmdBTRepository.restInitConfigState()
    }

}