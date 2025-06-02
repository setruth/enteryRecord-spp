package com.setruth.entityflowrecord.ui.pages.home

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class HomeViewModel(
    private val bluetoothRepository: BluetoothRepository
) : ViewModel() {
    val connectDevice = bluetoothRepository.connectedDevice
    fun disconnect() {
        bluetoothRepository.disconnect()
    }
}