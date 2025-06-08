package com.setruth.entityflowrecord.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * 基于协程的通信任务类，用于处理蓝牙设备的数据读写操作。
 * 
 * 该类使用 Kotlin 协程来异步处理数据流，支持挂起函数的数据接收回调，
 * 提供了启动、写入数据和取消操作的功能。
 * 
 * @param inputStream 输入流，用于从蓝牙设备读取数据
 * @param outputStream 输出流，用于向蓝牙设备写入数据
 * @param onError 错误回调函数，当发生通信错误时被调用
 * @param onDataReceived 数据接收回调函数（挂起函数），当接收到数据时被调用
 * 
 * @author EntityFlowRecord
 * @since 1.0
 */
class CommunicationTask(
    private val inputStream: InputStream,
    private val outputStream: OutputStream,
    private val onError: () -> Unit,
    private val onDataReceived: suspend (bytes: ByteArray, bytesRead: Int) -> Unit
) {
    /** 日志标签 */
    private val TAG = "CommunicationThread"
    
    /** 数据读取缓冲区，大小为1024字节 */
    private val buffer = ByteArray(1024)
    
    /** 通信协程任务 */
    private var communicationJob: Job? = null
    
    /** 协程作用域，使用IO调度器 */
    private val scope = CoroutineScope(Dispatchers.IO)
    
    /**
     * 启动通信任务。
     * 
     * 在IO线程中启动一个协程，持续从输入流读取数据。
     * 当读取到数据时，会调用 [onDataReceived] 回调函数。
     * 如果读取失败或流结束，会调用 [onError] 回调函数。
     * 
     * 注意：此方法可以重复调用，但只有在之前的任务被取消后才会启动新任务。
     */
    fun start() {
        communicationJob = scope.launch {
            try {
                while (isActive) {
                    val bytes = inputStream.read(buffer)
                    if (bytes == -1) {
                        onError()
                        break
                    }
                    onDataReceived(buffer, bytes)
                }
            } catch (e: Exception) {
                if (isActive) {
                    onError()
                }
            }
            Log.d(TAG, "通讯协程停止")
        }
    }

    /**
     * 向输出流写入数据。
     * 
     * 这是一个挂起函数，会在当前协程上下文中执行写入操作。
     * 由于协程作用域已经使用IO调度器，因此不需要额外的线程切换。
     * 
     * @param bytes 要写入的字节数组
     * @throws IOException 当写入操作失败时抛出
     * 
     * 注意：如果写入失败，会调用 [onError] 回调函数。
     */
    suspend fun write(bytes: ByteArray) {
        try {
            outputStream.write(bytes)
            Log.d(TAG, "发送数据给蓝牙: ${String(bytes)}")
        } catch (e: IOException) {
            Log.e(TAG, "发送错误", e)
            onError()
        }
    }

    /**
     * 取消通信任务并关闭流。
     * 
     * 此方法会：
     * 1. 取消正在运行的协程任务
     * 2. 关闭输入流和输出流
     * 3. 记录相关日志
     * 
     * 即使在关闭流时发生异常，也会记录错误日志但不会抛出异常。
     * 此方法是线程安全的，可以从任何线程调用。
     */
    fun cancel() {
        try {
            communicationJob?.cancel()
            inputStream.close()
            outputStream.close()
            Log.d(TAG, "CommunicationThread: Streams closed.")
        } catch (e: IOException) {
            Log.e(TAG, "CommunicationThread: Could not close streams", e)
        }
    }
}