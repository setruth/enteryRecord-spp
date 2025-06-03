package com.setruth.entityflowrecord.ui.pages.setting

import androidx.lifecycle.ViewModel
import com.setruth.entityflowrecord.data.model.ConfigKeys
import com.setruth.entityflowrecord.data.model.LightRange
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
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
    private val bluetoothRepository: BluetoothRepository
) : ViewModel() {
    private var _lightRange = MutableStateFlow(LightRange())
    val lightRange = _lightRange.asStateFlow()
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
                decodeInt(ConfigKeys.FULL_COUNT, 20)
            }
            _voiceRemindEnable.update {
                decodeBool(ConfigKeys.VOICE_REMIND_ENABLE, false)
            }
            _noEntranceEnable.update {
                decodeBool(ConfigKeys.NO_ENTRANCE_ENABLE, false)
            }
            _lightRange.update {
                LightRange(
                    warn = decodeInt(ConfigKeys.YELLOW_LIGHT_RANGE, 0),
                    err = decodeInt(ConfigKeys.RED_LIGHT_RANGE, 0),
                )
            }
        }
    }

    fun sendIntent(intent: SettingViewIntent) = when (intent) {
        is SettingViewIntent.UpdateLightRange -> {
            _lightRange.update { intent.lightRange }
            mmkv?.putInt(ConfigKeys.YELLOW_LIGHT_RANGE, _lightRange.value.warn)
            mmkv?.putInt(ConfigKeys.RED_LIGHT_RANGE, _lightRange.value.err)
        }

        is SettingViewIntent.UpdateFullCount -> {
            _fullCount.update { intent.fullCount }
            mmkv?.putInt(ConfigKeys.FULL_COUNT, _fullCount.value)
        }

        is SettingViewIntent.UpdateNoEntranceEnable -> {
            _noEntranceEnable.update { intent.enable }
            mmkv?.putBoolean(ConfigKeys.NO_ENTRANCE_ENABLE, _noEntranceEnable.value)

        }

        is SettingViewIntent.UpdateVoiceRemindEnable -> {
            _voiceRemindEnable.update { intent.enable }
            mmkv?.putBoolean(ConfigKeys.VOICE_REMIND_ENABLE, _voiceRemindEnable.value)
        }

    }

}