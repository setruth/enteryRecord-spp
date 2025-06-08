package com.setruth.entityflowrecord.ui.pages.home

import androidx.lifecycle.ViewModel
import com.setruth.entityflowrecord.data.repository.CMDBTRepository


class HomeViewModel(
    private val cmdBTRepository: CMDBTRepository
) : ViewModel() {
    val connectDevice = cmdBTRepository.connectedDevice
    fun disconnect() {
        cmdBTRepository.disconnect()
    }
}