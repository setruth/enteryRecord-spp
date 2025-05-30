package com.setruth.entityflowrecord.data.di

import android.bluetooth.BluetoothManager
import android.content.Context
import com.setruth.entityflowrecord.data.repository.BluetoothRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {

    single { androidContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }

    single { get<BluetoothManager>().adapter }

    singleOf(::BluetoothRepository)
}