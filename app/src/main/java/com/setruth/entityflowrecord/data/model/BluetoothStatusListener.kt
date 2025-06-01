package com.setruth.entityflowrecord.data.model

import android.bluetooth.BluetoothDevice

interface BluetoothStatusListener {
    /**
     * 当蓝牙扫描开始时调用。
     */
    fun onDiscoveryStarted() {}

    /**
     * 当蓝牙扫描结束时调用。
     */
    fun onDiscoveryFinished() {}

    /**
     * 每当发现一个新蓝牙设备时调用。
     * @param device 发现的蓝牙设备。
     */
    fun onDeviceFound(device: BluetoothDevice)
    /**
     * 蓝牙断开连接时候的回调
     * @param device 断开的设备。
     */
    fun onDisconnected(device: BluetoothDevice?)
    /**
     * 当绑定状态变为绑定成功时候的回调
     * @param device 断开的设备。
     */
    fun onBoundedState(device: BluetoothDevice?)

}