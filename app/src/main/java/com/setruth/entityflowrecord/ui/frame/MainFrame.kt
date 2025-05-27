package com.setruth.entityflowrecord.ui.frame

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.setruth.entityflowrecord.components.MaskBox
import com.setruth.entityflowrecord.model.MaskAnimModel
import com.setruth.entityflowrecord.model.ThemeMode
import com.setruth.entityflowrecord.ui.pages.home.HomeView
import com.setruth.entityflowrecord.ui.pages.notification.NotificationView
import com.setruth.entityflowrecord.ui.pages.setting.SettingView
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainFrame(startIndex: Int = 0) {
    var selectedItemIndex by remember { mutableIntStateOf(startIndex) }
    val navController = rememberNavController()
    var themeMode by remember { mutableStateOf(ThemeMode.LIGHT) }
    EntityFlowRecordTheme(themeMode) {
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
                    BottomNavigationBar(selectedItemIndex, navController) { newSelectIndex ->
                        selectedItemIndex = newSelectIndex
                    }
                },
                topBar = {
                    TopBar(navItems[selectedItemIndex].topBarTitle, themeMode) { newThemeModeInfo ->
                        themeMode = newThemeModeInfo.newMode
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
                        navController = navController,
                        startDestination = navItems[selectedItemIndex].route,
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
                        composable("home") { HomeView(themeMode) }
                        composable("notifications") { NotificationView() }
                        composable("settings") { SettingView() }
                    }
                }
            }
        }

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun GreetingPreview() {
    MainFrame()
}