package com.setruth.entityflowrecord.ui.frame

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.setruth.entityflowrecord.components.MaskBox
import com.setruth.entityflowrecord.data.di.appModule
import com.setruth.entityflowrecord.data.model.MaskAnimModel
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.data.model.mainNavItems
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import com.setruth.entityflowrecord.data.repository.CMDBTRepository
import com.setruth.entityflowrecord.ui.pages.home.HomeView
import com.setruth.entityflowrecord.ui.pages.notification.NotificationView
import com.setruth.entityflowrecord.ui.pages.setting.SettingView
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme
import org.koin.compose.KoinApplicationPreview
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainFrame(
    startIndex: Int = 0,
    themeMode: ThemeMode = ThemeMode.LIGHT,
    themeChange: (ThemeMode) -> Unit = {},
    navToDevices: () -> Unit = {}
) {
    val bluetoothRepository = koinInject<CMDBTRepository>()
    val connectedDevice by bluetoothRepository.connectedDevice.collectAsState()
    var selectedItemIndex by remember { mutableIntStateOf(startIndex) }
    val mainNavController = rememberNavController()
    MaskBox(
        animTime = 500,
        maskComplete = {},
        animFinish = {}
    ) { maskAnimActiveEvent ->
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            bottomBar = {
                BottomNavigationBar(selectedItemIndex, mainNavController) { newSelectIndex ->
                    selectedItemIndex = newSelectIndex
                }
            },
            topBar = {
                TopBar(
                    connectedDevice != null,
                    mainNavItems[selectedItemIndex].topBarTitle,
                    themeMode,
                ) { newThemeModeInfo ->
                    themeChange(newThemeModeInfo.newMode)
                    when (themeMode) {
                        ThemeMode.DARK -> maskAnimActiveEvent(
                            MaskAnimModel.EXPEND,
                            newThemeModeInfo.clickX,
                            newThemeModeInfo.clickY
                        )

                        ThemeMode.LIGHT -> maskAnimActiveEvent(
                            MaskAnimModel.SHRINK,
                            newThemeModeInfo.clickX,
                            newThemeModeInfo.clickY
                        )
                    }
                }
            }
        ) { paddingValue ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue)
                    .padding(horizontal = 15.dp)
                    .padding(bottom = 10.dp)
            ) {
                NavHost(
                    navController = mainNavController,
                    startDestination = mainNavItems[selectedItemIndex].route,
                    enterTransition = {
                        slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn()
                    },
                    exitTransition = {
                        slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut()
                    },
                    popEnterTransition = {
                        slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) + fadeIn()
                    },
                    popExitTransition = {
                        slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut()
                    }
                ) {
                    composable("home") { HomeView(navToDevices) }
                    composable("notifications") { NotificationView() }
                    composable("settings") { SettingView() }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(KoinExperimentalAPI::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun GreetingPreview() {
    KoinApplicationPreview(application = {
        modules(appModule, module {
            single { BluetoothRepository() }
        })
    }) {
        MainFrame(0)
    }
}