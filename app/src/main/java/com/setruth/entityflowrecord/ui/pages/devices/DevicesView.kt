package com.setruth.entityflowrecord.ui.pages.devices

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setruth.entityflowrecord.data.di.appModule
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import com.setruth.entityflowrecord.ui.frame.MainFrame
import com.setruth.entityflowrecord.ui.pages.home.components.DeviceDialog
import com.setruth.entityflowrecord.ui.pages.home.components.NewestNotificationCard
import com.setruth.entityflowrecord.ui.pages.home.components.PeopleCountCard
import com.setruth.entityflowrecord.ui.pages.home.components.SystemStatusCard
import com.setruth.entityflowrecord.ui.pages.home.components.UploadTimeCard
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplicationPreview
import org.koin.dsl.module
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesView(themeMode: ThemeMode, onFinish: () -> Unit = {}) =
    EntityFlowRecordTheme(themeMode) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("蓝牙设备") },
                    navigationIcon = {
                        IconButton(onClick = onFinish) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {

            }
        }
    }

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun DevicesViewPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        DevicesView(ThemeMode.LIGHT)
    }
}