package com.setruth.entityflowrecord.data.di

import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import com.setruth.entityflowrecord.ui.pages.devices.DevicesView
import com.setruth.entityflowrecord.ui.pages.home.HomeViewModel
import com.setruth.entityflowrecord.ui.pages.devices.DevicesViewModel
import com.setruth.entityflowrecord.ui.pages.setting.SettingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::DevicesViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingViewModel)
}