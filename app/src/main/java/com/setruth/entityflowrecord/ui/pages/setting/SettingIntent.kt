package com.setruth.entityflowrecord.ui.pages.setting

import androidx.lifecycle.ViewModel
import com.setruth.entityflowrecord.data.model.ConfigKeys
import com.setruth.entityflowrecord.data.model.DEFAULT_ALARM_LIGHT
import com.setruth.entityflowrecord.data.model.DEFAULT_BUZZ_ON
import com.setruth.entityflowrecord.data.model.DEFAULT_ERR_LIGHT
import com.setruth.entityflowrecord.data.model.DEFAULT_FULL_STOP_ON
import com.setruth.entityflowrecord.data.model.DEFAULT_MAX_COUNT
import com.setruth.entityflowrecord.data.model.LightRange
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import com.setruth.entityflowrecord.data.repository.CMDBTRepository
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed class SettingViewIntent {
    data class UpdateLightRange(val lightRange: LightRange) : SettingViewIntent()
    data class UpdateFullCount(val fullCount: Int) : SettingViewIntent()
    data class UpdateVoiceRemindEnable(val enable: Boolean) : SettingViewIntent()
    data class UpdateNoEntranceEnable(val enable: Boolean) : SettingViewIntent()
}

class SettingViewModel(
    private val cmdBTRepository: CMDBTRepository
) : ViewModel() {
    private var _lightRange = MutableStateFlow(LightRange())
    val lightRange = _lightRange.asStateFlow()
    private var _configUpdateState = MutableStateFlow<ConfigUpdateState>(ConfigUpdateState.None)
    val configUpdateState = _configUpdateState.asStateFlow()
    private var _fullCount = MutableStateFlow(11)
    val fullCount = _fullCount.asStateFlow()
    private var _voiceRemindEnable = MutableStateFlow(false)
    val voiceRemindEnable = _voiceRemindEnable.asStateFlow()
    private var _noEntranceEnable = MutableStateFlow(false)
    val noEntranceEnable = _noEntranceEnable.asStateFlow()
    private val mmkv = try {
        MMKV.defaultMMKV()
    } catch (_: Exception) {
        null
    }

    init {
        mmkv?.apply {
            _fullCount.update {
                decodeInt(ConfigKeys.FULL_COUNT, DEFAULT_MAX_COUNT)
            }
            _voiceRemindEnable.update {
                decodeBool(ConfigKeys.VOICE_REMIND_ENABLE, DEFAULT_BUZZ_ON)
            }
            _noEntranceEnable.update {
                decodeBool(ConfigKeys.NO_ENTRANCE_ENABLE, DEFAULT_FULL_STOP_ON)
            }
            _lightRange.update {
                LightRange(
                    warn = decodeInt(ConfigKeys.YELLOW_LIGHT_RANGE, DEFAULT_ALARM_LIGHT),
                    err = decodeInt(ConfigKeys.RED_LIGHT_RANGE, DEFAULT_ERR_LIGHT),
                )
            }
        }
    }

    fun sendIntent(intent: SettingViewIntent) = when (intent) {
        is SettingViewIntent.UpdateLightRange -> {
            updateLightRange(intent.lightRange)
        }

        is SettingViewIntent.UpdateFullCount -> {
            updateFullCount(intent.fullCount)
        }

        is SettingViewIntent.UpdateNoEntranceEnable -> {
            updateNoEntranceEnable(intent.enable)

        }

        is SettingViewIntent.UpdateVoiceRemindEnable -> {
            updateVoiceRemindEnable(intent.enable)
        }

    }

   private fun updateFullCount(newCount: Int) = sendCMDHandle(
        onSendBlock = {
            cmdBTRepository.setNewMaxCount(newCount)
        },
        onSuccess = {
            _fullCount.update { newCount }
            mmkv?.putInt(ConfigKeys.FULL_COUNT, _fullCount.value)
        },
    )

    private fun updateVoiceRemindEnable(enable: Boolean) = sendCMDHandle(
        onSendBlock = {
            if (enable) {
                cmdBTRepository.enableBuzzer()
            } else {
                cmdBTRepository.disableBuzzer()
            }
        },
        onSuccess = {
            _voiceRemindEnable.update { enable }
            mmkv?.putBoolean(ConfigKeys.VOICE_REMIND_ENABLE, _voiceRemindEnable.value)
        },
    )

    private  fun updateNoEntranceEnable(enable: Boolean) = sendCMDHandle(
        onSendBlock = {
            if (enable) {
                cmdBTRepository.enableFullStop()
            } else {
                cmdBTRepository.disableFullStop()
            }
        },
        onSuccess = {
            _noEntranceEnable.update { enable }
            mmkv?.putBoolean(ConfigKeys.NO_ENTRANCE_ENABLE, _noEntranceEnable.value)
        },
    )

    private fun updateLightRange(lightRange: LightRange) {
        sendCMDHandle(
            onSendBlock = {
                if (lightRange.warn != _lightRange.value.warn && lightRange.err != _lightRange.value.err) {
                    cmdBTRepository.setNewAlarmAndErrorLight(lightRange.warn, lightRange.err)
                } else if (lightRange.warn != _lightRange.value.warn) {
                    cmdBTRepository.setNewAlarmLight(lightRange.warn)
                } else if (lightRange.err != _lightRange.value.err) {
                    cmdBTRepository.setNewErrorLight(lightRange.err)
                } else true
            },
            onSuccess = {
                _lightRange.update { lightRange }
                mmkv?.putInt(ConfigKeys.YELLOW_LIGHT_RANGE, _lightRange.value.warn)
                mmkv?.putInt(ConfigKeys.RED_LIGHT_RANGE, _lightRange.value.err)
            }
        )
    }

    private fun sendCMDHandle(
        onSendBlock: () -> Boolean,
        onSuccess: () -> Unit,
        onFailed: () -> Unit = {}
    ) {
        _configUpdateState.value = ConfigUpdateState.Loading
        if (onSendBlock()) {
            onSuccess();
            _configUpdateState.value = ConfigUpdateState.Success
        } else {
            onFailed();
            _configUpdateState.value = ConfigUpdateState.Failed
        }
    }
}

sealed class ConfigUpdateState {
    object None : ConfigUpdateState()
    object Loading : ConfigUpdateState()
    object Success : ConfigUpdateState()
    object Failed : ConfigUpdateState()
}