package com.setruth.entityflowrecord.ui.pages.setting

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import com.setruth.entityflowrecord.data.repository.BluetoothConnectionState
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import com.setruth.entityflowrecord.data.repository.CMDBTRepository
import com.setruth.entityflowrecord.data.repository.InitConfigState
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
    val configUpdateState by viewModel.configUpdateState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(configUpdateState) {
        when (configUpdateState) {
            is ConfigUpdateState.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(message = "更新成功")
                }
            }

            is ConfigUpdateState.Failed -> {
                snackbarHostState.showSnackbar(message = "更新失败,请稍后重试")
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { _ ->

        if (configUpdateState == ConfigUpdateState.Loading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "正在更新配置",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                return@Scaffold
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                FullCountCard(fullCount = fullCount) {
                    viewModel.sendIntent(SettingViewIntent.UpdateFullCount(it))
                }
            }
            item {
                ThresholdConfigCard(lightRange = lightRange) {
                    viewModel.sendIntent(SettingViewIntent.UpdateLightRange(it))
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
                single { CMDBTRepository() }
            })
        }) {
            MainFrame(2)
        }
    }
}