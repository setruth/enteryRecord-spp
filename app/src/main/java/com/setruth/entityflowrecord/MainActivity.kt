package com.setruth.entityflowrecord

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.setruth.entityflowrecord.data.di.appModule
import com.setruth.entityflowrecord.data.model.ConfigKeys
import com.setruth.entityflowrecord.data.model.MainNavItem
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.data.model.appNavItems
import com.setruth.entityflowrecord.data.model.mainNavItems
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import com.setruth.entityflowrecord.ui.frame.MainFrame
import com.setruth.entityflowrecord.ui.pages.devices.DevicesView
import com.setruth.entityflowrecord.ui.pages.home.HomeView
import com.setruth.entityflowrecord.ui.pages.notification.NotificationView
import com.setruth.entityflowrecord.ui.pages.setting.SettingView
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme
import com.setruth.entityflowrecord.ui.pages.splash.SplashView
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.scope.ScopeActivity
import org.koin.compose.KoinApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    private lateinit var requestBluetoothEnableLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestLocationEnableLauncher: ActivityResultLauncher<Intent>
    private var bluetoothRepository: BluetoothRepository? = null
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            if (allGranted) {
                Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT)
                checkAndProceedWithBluetoothOperations()

            } else {
                Toast.makeText(this, "无权限将3秒后退出软件", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    delay(3.seconds)
                    withContext(Dispatchers.Main) {
                        finish()
                    }
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MMKV.initialize(this)
        val mmkv = MMKV.defaultMMKV()
        enableEdgeToEdge()
        requestBluetoothEnableLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    checkAndProceedWithBluetoothOperations()
                } else {
                    Toast.makeText(this, "蓝牙未开启无法扫描设备", Toast.LENGTH_SHORT).show()
                }
            }

        requestLocationEnableLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
                checkAndProceedWithBluetoothOperations()
            }
        requestBluetoothPermissions()
        val blueAdapter = (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
        bluetoothRepository = BluetoothRepository(this, blueAdapter)
        val defaultThemeMode=  if (mmkv.getBoolean(ConfigKeys.IS_DARK_MODE, false)) {
            ThemeMode.DARK
        } else {
            ThemeMode.LIGHT
        }
        setContent {
            KoinApplication(application = {
                androidContext(this@MainActivity)
                modules(appModule, module {
                    single { bluetoothRepository }
                })
            }) {
                val appNavController = rememberNavController()
                var selectedItemIndex by remember { mutableIntStateOf(0) }
                var themeMode by remember { mutableStateOf(defaultThemeMode) }
                EntityFlowRecordTheme(themeMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        NavHost(
                            navController = appNavController,
                            startDestination = appNavItems[0].route,
                            enterTransition = {
                                // 根据不同的页面切换使用不同的动画
                                when (targetState.destination.route) {
                                    appNavItems[0].route -> {
                                        // 开屏页面：淡入效果
                                        fadeIn(
                                            animationSpec = tween(
                                                durationMillis = 300,
                                                easing = LinearOutSlowInEasing
                                            )
                                        )
                                    }
                                    appNavItems[1].route -> {
                                        // 主页面：从右侧滑入
                                        slideInHorizontally(
                                            initialOffsetX = { fullWidth -> fullWidth },
                                            animationSpec = tween(
                                                durationMillis = 400,
                                                easing = FastOutSlowInEasing
                                            )
                                        ) + fadeIn(
                                            animationSpec = tween(
                                                durationMillis = 400,
                                                easing = LinearOutSlowInEasing
                                            )
                                        )
                                    }
                                    appNavItems[2].route -> {
                                        // 设备页面：从右侧滑入
                                        slideInHorizontally(
                                            initialOffsetX = { fullWidth -> fullWidth },
                                            animationSpec = tween(
                                                durationMillis = 350,
                                                easing = FastOutSlowInEasing
                                            )
                                        ) + fadeIn(
                                            animationSpec = tween(
                                                durationMillis = 350
                                            )
                                        )
                                    }
                                    else -> {
                                        // 默认动画
                                        fadeIn(animationSpec = tween(300))
                                    }
                                }
                            },
                            exitTransition = {
                                when (initialState.destination.route) {
                                    appNavItems[0].route -> {
                                        // 开屏页面：淡出效果
                                        fadeOut(
                                            animationSpec = tween(
                                                durationMillis = 200,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                    }
                                    appNavItems[1].route -> {
                                        // 主页面：向左滑出
                                        slideOutHorizontally(
                                            targetOffsetX = { fullWidth -> -fullWidth },
                                            animationSpec = tween(
                                                durationMillis = 350,
                                                easing = FastOutSlowInEasing
                                            )
                                        ) + fadeOut(
                                            animationSpec = tween(
                                                durationMillis = 350
                                            )
                                        )
                                    }
                                    appNavItems[2].route -> {
                                        // 设备页面：向左滑出
                                        slideOutHorizontally(
                                            targetOffsetX = { fullWidth -> -fullWidth },
                                            animationSpec = tween(
                                                durationMillis = 300,
                                                easing = FastOutSlowInEasing
                                            )
                                        ) + fadeOut(
                                            animationSpec = tween(
                                                durationMillis = 300
                                            )
                                        )
                                    }
                                    else -> {
                                        fadeOut(animationSpec = tween(250))
                                    }
                                }
                            },
                            popEnterTransition = {
                                // 返回时的进入动画：从左侧滑入
                                slideInHorizontally(
                                    initialOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = tween(
                                        durationMillis = 350,
                                        easing = FastOutSlowInEasing
                                    )
                                ) + fadeIn(
                                    animationSpec = tween(
                                        durationMillis = 350
                                    )
                                )
                            },
                            popExitTransition = {
                                // 返回时的退出动画：向右滑出
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> fullWidth },
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = FastOutSlowInEasing
                                    )
                                ) + fadeOut(
                                    animationSpec = tween(
                                        durationMillis = 300
                                    )
                                )
                            }
                        ) {
                            // 开屏页面
                            composable(appNavItems[0].route) {
                                SplashView {
                                    appNavController.navigate(appNavItems[1].route) {
                                        popUpTo(appNavItems[0].route) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            }
                            // 主页面
                            composable(appNavItems[1].route) {
                                MainFrame(themeMode = themeMode, themeChange = {
                                    themeMode = it
                                    mmkv.putBoolean(ConfigKeys.IS_DARK_MODE, it == ThemeMode.DARK)
                                }) {
                                    appNavController.navigate(appNavItems[2].route) {
                                        selectedItemIndex = 1
                                        popUpTo(appNavController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                            // 设备页面
                            composable(appNavItems[2].route) {
                                DevicesView {
                                    selectedItemIndex = 0
                                    appNavController.popBackStack()
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private fun requestBluetoothPermissions() {
        val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        val neededPermissions = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (neededPermissions.isNotEmpty()) {
            requestPermissionsLauncher.launch(neededPermissions.toTypedArray())
        } else {
            checkAndProceedWithBluetoothOperations()
        }
    }

    private fun checkAndProceedWithBluetoothOperations() {
        ensureBluetoothAndLocationEnabled(
            activityContext = this, // 或者 requireContext() 如果在 Fragment 中
            requestBluetoothEnableLauncher = requestBluetoothEnableLauncher,
            requestLocationEnableLauncher = requestLocationEnableLauncher
        ) {
        }
    }

    /**
     * 引导用户打开蓝牙和定位服务。
     * 此函数假设所有必要的运行时权限（BLUETOOTH_SCAN, BLUETOOTH_CONNECT, ACCESS_FINE_LOCATION）已经获得。
     *
     * @param activityContext Activity 或 Fragment 的上下文，用于启动 Intent。
     * @param requestBluetoothEnableLauncher 用于启动蓝牙开启请求的 ActivityResultLauncher。
     * @param requestLocationEnableLauncher 用于启动定位服务开启请求的 ActivityResultLauncher。
     * @param onAllConditionsMet 当所有蓝牙和定位条件都满足时调用的回调函数。
     */
    private fun ensureBluetoothAndLocationEnabled(
        activityContext: Context,
        requestBluetoothEnableLauncher: ActivityResultLauncher<Intent>,
        requestLocationEnableLauncher: ActivityResultLauncher<Intent>,
        onAllConditionsMet: () -> Unit
    ) {
        val bluetoothManager =
            activityContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            Toast.makeText(this, "请打开蓝牙以便设备扫描", Toast.LENGTH_SHORT).show()
            requestBluetoothEnableLauncher.launch(enableBtIntent)
            return
        }

        val locationManager =
            activityContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

        if (!isLocationEnabled) {
            val enableLocationIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            Toast.makeText(this, "请打开定位以便设备扫描", Toast.LENGTH_SHORT).show()

            requestLocationEnableLauncher.launch(enableLocationIntent)
            return
        }

        onAllConditionsMet()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothRepository?.unregister()
    }
}

