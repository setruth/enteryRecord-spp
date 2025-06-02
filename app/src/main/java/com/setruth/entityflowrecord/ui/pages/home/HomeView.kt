package com.setruth.entityflowrecord.ui.pages.home

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setruth.entityflowrecord.components.DisconnectConfirmDialog
import com.setruth.entityflowrecord.data.di.appModule
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import com.setruth.entityflowrecord.ui.frame.MainFrame
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeView(
    toConnect: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val connectDevice by viewModel.connectDevice.collectAsState()
    var disconnectConfirmDialogShow by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        //总览一张卡
        PeopleCountCard(
            currentPeople = 28,
            maxCapacity = 50,
        )
        //设备信息卡片
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.height(170.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            SystemStatusCard(
                connectDevice = connectDevice,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(1f),
                connect = {
                    toConnect()
                },
                disconnect = {
                    disconnectConfirmDialogShow = true
                }
            )
            UploadTimeCard(
                true, modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
        //今日人员曲线
        Spacer(modifier = Modifier.height(10.dp))

        NewestNotificationCard()
    }
    if (disconnectConfirmDialogShow) {
        DisconnectConfirmDialog(
            device = connectDevice,
            onConfirm = {
                viewModel.disconnect()
                Toast.makeText(context, "正在断开连接", Toast.LENGTH_SHORT).show()
            }, onCancel = {
                disconnectConfirmDialogShow = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun HomeViewPreview() {
    EntityFlowRecordTheme {
        KoinApplicationPreview(application = {
            modules(appModule, module {
                single { BluetoothRepository() }
            })
        }) {
            MainFrame(0)
        }
    }
}