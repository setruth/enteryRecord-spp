package com.setruth.entityflowrecord.util

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 根据 BluetoothClass.Device.Major（主设备类）获取对应的 Material Design 图标。
 *
 * @param majorDeviceClass BluetoothClass.Device.Major 常量之一。
 * @return 对应的 ImageVector 图标，如果无法匹配则返回 Icons.Default.Bluetooth。
 */
fun getIconForMajorDeviceClass(majorDeviceClass: Int): ImageVector {
    return when (majorDeviceClass) {
        BluetoothClass.Device.Major.AUDIO_VIDEO -> Icons.Outlined.Headphones // 音频/视频设备 (耳机, 扬声器, 电视等)
        BluetoothClass.Device.Major.COMPUTER -> Icons.Outlined.Computer       // 电脑 (台式机, 笔记本, PDA等)
        BluetoothClass.Device.Major.PHONE -> Icons.Outlined.PhoneAndroid      // 手机 (蜂窝电话, 智能手机等)
        BluetoothClass.Device.Major.HEALTH -> Icons.Outlined.MedicalServices  // 医疗健康设备
        BluetoothClass.Device.Major.WEARABLE -> Icons.Outlined.Watch         // 可穿戴设备 (智能手表, 眼镜等)
        BluetoothClass.Device.Major.PERIPHERAL -> Icons.Outlined.Keyboard    // 外设 (鼠标, 键盘, 游戏杆等)
        BluetoothClass.Device.Major.IMAGING -> Icons.Outlined.Print          // 图像设备 (打印机, 扫描仪等)
        BluetoothClass.Device.Major.NETWORKING,      // 网络设备
        BluetoothClass.Device.Major.TOY,             // 玩具
        BluetoothClass.Device.Major.UNCATEGORIZED,   // 未分类
        BluetoothClass.Device.Major.MISC             // 杂项
            -> Icons.Outlined.Bluetooth // 对于不明确的类别，使用通用蓝牙图标
        else -> Icons.Outlined.Bluetooth // 默认图标
    }
}