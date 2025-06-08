package com.setruth.entityflowrecord.data.repository

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.setruth.entityflowrecord.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class CMDBTRepository(
    context: Context? = null,
    bluetoothAdapter: BluetoothAdapter? = null,
) : BluetoothRepository(context, bluetoothAdapter) {

    private var _initConfigState =
        MutableStateFlow<InitConfigState>(InitConfigState.None)
    val initConfigState = _initConfigState.asStateFlow()

    companion object {
        private const val TAG = "CMDBTRepository"
    }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            connectionState.collect {
                if (it == BluetoothConnectionState.Connected) {
                    sendConnectCommand()
                }
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            receivingCommandFlow.collect {
                Log.d(TAG, "接收到了指令:$it ")
                if (CMD_CONNECT_SUCCESS == it) {
                    _initConfigState.value = InitConfigState.Need
                }
                if (CMD_CONFIG_INIT_FINISH == it){
                    _initConfigState.value= InitConfigState.Done
                }
            }
        }
    }

    /**
     * 发送原始指令
     * @param command 要发送的指令字符串
     * @return 是否发送成功
     */
    private fun sendCommand(command: String): Boolean {
        return try {
            when (val stream = ioStream.value) {
                is BluetoothIoStream.Connected -> {
                    stream.outputStream.write(command.toByteArray())
                    Log.d(TAG, "发送指令: $command")
                    true
                }

                BluetoothIoStream.Disconnected -> {
                    Log.w(TAG, "蓝牙未连接，无法发送指令: $command")
                    false
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "发送指令失败: $command", e)
            false
        }
    }

    /**
     * 发送连接指令
     */
    private fun sendConnectCommand(): Boolean {
        return sendCommand(CMD_CONNECT)
    }

    /**
     * 发送断开连接指令
     */
    fun sendDisconnectCommand(): Boolean {
        return sendCommand(CMD_DISCONNECT)
    }

    /**
     * 开启蜂鸣器提示
     */
    fun enableBuzzer(): Boolean {
        return sendCommand(CMD_BUZZ_ON)
    }

    /**
     * 关闭蜂鸣器提示
     */
    fun disableBuzzer(): Boolean {
        return sendCommand(CMD_BUZZ_OFF)
    }

    /**
     * 开启满员禁止进入
     */
    fun enableFullStop(): Boolean {
        return sendCommand(CMD_FULL_STOP_ON)
    }

    /**
     * 关闭满员禁止进入
     */
    fun disableFullStop(): Boolean {
        return sendCommand(CMD_FULL_STOP_OFF)
    }

    /**
     * 设置新的最大容量
     * @param newCount 新的最大容量值
     */
    fun setNewMaxCount(newCount: Int): Boolean {
        return try {
            val command = getNewMaxCountCMD(newCount)
            sendCommand(command)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "设置最大容量参数错误: $newCount", e)
            false
        }
    }

    /**
     * 设置新的警告灯阈值
     * @param value 警告灯阈值
     */
    fun setNewAlarmLight(value: Int): Boolean {
        return try {
            val command = getNewAlarmLightCMD(value)
            sendCommand(command)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "设置警告灯阈值参数错误: $value", e)
            false
        }
    }

    /**
     * 设置新的错误灯阈值
     * @param value 错误灯阈值
     */
    fun setNewErrorLight(value: Int): Boolean {
        return try {
            val command = getNewErrLightCMD(value)
            sendCommand(command)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "设置错误灯阈值参数错误: $value", e)
            false
        }
    }

    /**
     * 发送STM初始化指令
     * @param maxCount 最大容量
     * @param currentCount 当前计数
     * @param fullStopOn 是否开启满员是否禁止入内 (1开启, 0关闭)
     * @param buzzOn 是否开启蜂鸣器 (1开启, 0关闭)
     * @param alarmLight 警告灯阈值
     * @param errLight 错误灯阈值
     */
    fun sendSTMInit(
        maxCount: Int,
        currentCount: Int,
        fullStopOn: Int,
        buzzOn: Int,
        alarmLight: Int,
        errLight: Int
    ): Boolean {
        val command =
            getSTMInitCMD(maxCount, currentCount, fullStopOn, buzzOn, alarmLight, errLight)
        return sendCommand(command)
    }
    fun restInitConfigState(){
        _initConfigState.value= InitConfigState.None
    }
}

sealed class InitConfigState {
    object None : InitConfigState()
    object Need : InitConfigState()
    object Done : InitConfigState()
}