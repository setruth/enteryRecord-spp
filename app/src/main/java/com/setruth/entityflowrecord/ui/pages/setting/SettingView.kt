package com.setruth.entityflowrecord.ui.pages.setting

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.setruth.entityflowrecord.data.di.appModule
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import com.setruth.entityflowrecord.ui.frame.MainFrame
import com.setruth.entityflowrecord.ui.pages.setting.components.FullCountCard
import com.setruth.entityflowrecord.ui.pages.setting.components.OtherSettingsCard
import com.setruth.entityflowrecord.ui.pages.setting.components.ThresholdConfigCard
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplicationPreview
import org.koin.dsl.module

@Composable
fun SettingView(
    viewModel: SettingViewModel = koinViewModel(),
) {
    val lightRange by viewModel.lightRange.collectAsState()
    val fullCount by viewModel.fullCount.collectAsState()
    val voiceRemindEnable by viewModel.voiceRemindEnable.collectAsState()
    val noEntranceEnable by viewModel.noEntranceEnable.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { _ ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                FullCountCard(fullCount = fullCount) {
                    viewModel.sendIntent(SettingViewIntent.UpdateFullCount(it))
                    scope.launch {
                        snackbarHostState.showSnackbar(message = "正在设置最大容量")

                    }
                }
            }
            item {
                ThresholdConfigCard(lightRange = lightRange) {
                    viewModel.sendIntent(SettingViewIntent.UpdateLightRange(it))
                    scope.launch {
                        snackbarHostState.showSnackbar(message = "正在设置阈值")
                    }
                }
            }
            item {
                OtherSettingsCard(
                    voiceRemindEnable = voiceRemindEnable,
                    noEntranceEnable = noEntranceEnable,
                    onVoiceRemindChange = {
                        viewModel.sendIntent(SettingViewIntent.UpdateVoiceRemindEnable(it))
                    },
                    noEntranceEnableChange = {
                        viewModel.sendIntent(SettingViewIntent.UpdateNoEntranceEnable(it))
                    }
                )
            }
        }
    }

}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun SettingViewPreview() {

    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        KoinApplicationPreview(application = {
            modules(appModule, module {
                single { BluetoothRepository() }
            })
        }) {
            MainFrame(2)
        }
    }
}