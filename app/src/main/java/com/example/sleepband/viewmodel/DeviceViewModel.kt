package com.example.sleepband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleepband.ble.BleConnectionState
import com.example.sleepband.ble.BleManager
import com.example.sleepband.ble.BleScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val bleScanner: BleScanner,
    private val bleManager: BleManager
) : ViewModel() {

    val isScanning = bleScanner.isScanning
    val foundDevices = bleScanner.foundDevices
    val connectionState = bleManager.connectionState

    fun startScan() {
        bleScanner.startScanning()
    }

    fun stopScan() {
        bleScanner.stopScanning()
    }

    fun connectToDevice(address: String) {
        bleScanner.stopScanning()
        bleManager.connect(address)
    }

    fun disconnect() {
        bleManager.disconnect()
    }
}
