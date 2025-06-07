package com.setruth.entityflowrecord.util

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

class CommunicationThread(
    private val inputStream: InputStream,
    private val outputStream: OutputStream,
    private val onError: () -> Unit, // 连接断开或错误时的回调
    private val onDataReceived: (bytes: ByteArray, bytesRead: Int) -> Unit // 接收到数据时的回调
) : Thread() {
    private  val TAG = "CommunicationThread"
    private val buffer = ByteArray(1024) // 缓冲区大小

    override fun run() {
        var bytes: Int
        while (!isInterrupted) {
            try {
                bytes = inputStream.read(buffer)
                if (bytes == -1) {
                    onError()
                    break
                }
                onDataReceived(buffer, bytes)
            } catch (e: Exception) {
                onError()
                break
            }
        }
        Log.d(TAG, "通讯线程停止")
    }

    fun write(bytes: ByteArray) {
        try {
            outputStream.write(bytes)
            Log.d(TAG, "发送数据给蓝牙: ${String(bytes)}")
        } catch (e: IOException) {
            Log.e(TAG, "发送错误", e)
            onError()
        }
    }

    fun cancel() {
        try {
            interrupt()
            inputStream.close()
            outputStream.close()
            Log.d(TAG, "CommunicationThread: Streams closed.")
        } catch (e: IOException) {
            Log.e(TAG, "CommunicationThread: Could not close streams", e)
        }
    }
}