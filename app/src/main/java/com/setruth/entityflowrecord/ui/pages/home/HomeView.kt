package com.setruth.entityflowrecord.ui.pages.home

import android.os.Build
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.setruth.entityflowrecord.model.ThemeMode
import com.setruth.entityflowrecord.ui.frame.MainFrame
import com.setruth.entityflowrecord.ui.pages.home.components.NewestNotificationCard
import com.setruth.entityflowrecord.ui.pages.home.components.PeopleCountCard
import com.setruth.entityflowrecord.ui.pages.home.components.SystemStatusCard
import com.setruth.entityflowrecord.ui.pages.home.components.UploadTimeCard
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// 辅助函数：格式化时间，用于“当前时间”卡片
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun rememberCurrentTime(): String {
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    var currentTime by remember { mutableStateOf(LocalTime.now().format(formatter)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = LocalTime.now().format(formatter)
        }
    }
    return currentTime
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeView(
    themeMode: ThemeMode,
) {
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
                true, modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(1f)
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
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun HomeViewPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        MainFrame(0)
    }
}