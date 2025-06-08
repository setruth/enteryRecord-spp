package com.setruth.entityflowrecord.data.model


/**
 * 命令常量定义
 */
const val CMD_ENDING = '~'
const val CMD_START = '*'

/**
 * 连接命令
 */
const val CMD_CONNECT = "${CMD_START}setruth$CMD_ENDING"

/**
 * 断开连接指令
 */
const val CMD_DISCONNECT = "${CMD_START}disconnect$CMD_ENDING"

/**
 * 蜂鸣器提示开启指令
 */
const val CMD_BUZZ_ON = "${CMD_START}buzz_1$CMD_ENDING"

/**
 * 蜂鸣器提示关闭指令
 */
const val CMD_BUZZ_OFF = "${CMD_START}buzz_0$CMD_ENDING"

/**
 * 满员禁止进入开启指令
 */
const val CMD_FULL_STOP_ON = "${CMD_START}full_1$CMD_ENDING"

/**
 * 满员禁止进入关闭指令
 */
const val CMD_FULL_STOP_OFF = "${CMD_START}full_0$CMD_ENDING"

/**
 * 获取设置最大容量指令
 *
 */
fun getNewMaxCountCMD(newCount: Int): String {
    require(newCount > 0) { "新的最大容量应该大于0" }
    return "${CMD_START}new_max:$newCount$CMD_ENDING"
}

/**
 * 获取设置警告灯阈值值指令
 *
 */
fun getNewAlarmLightCMD(value: Int): String {
    require(value > 0) { "新的警告值应该大于0" }
    return "${CMD_START}new_alarm:$value$CMD_ENDING"
}

/**
 * 获取设置错误灯阈值值指令
 *
 */
fun getNewErrLightCMD(value: Int): String {
    require(value > 0) { "新的警告值应该大于0" }
    return "${CMD_START}new_err:$value$CMD_ENDING"
}

fun getNewAlarmAndErrLightCMD(alarm: Int, err: Int): String {
    require(alarm > 0 && err > 0) { "新的值应该大于0" }
    return "${CMD_START}new_light:${alarm};${err}$CMD_ENDING"
}

/**
 * 获取初始化STM相关配置指令
 */
fun getSTMInitCMD(
    maxCount: Int,
    currentCount: Int,
    fullStopOn: Int,
    buzzOn: Int,
    alarmLight: Int,
    errLight: Int
): String {
    return "${CMD_START}init:${maxCount};${currentCount};${fullStopOn};${buzzOn};${alarmLight};${errLight}$CMD_ENDING"
}

//接收指令
const val CMD_CONNECT_SUCCESS = "connectOK"

const val CMD_CONFIG_INIT_FINISH = "configInitOK"