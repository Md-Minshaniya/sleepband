package com.example.sleepband.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sleepband.ble.BleManager
import com.example.sleepband.ble.BleScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val bleScanner: BleScanner,
    private val bleManager: BleManager
) : ViewModel() {

    /* ============================= */
    /* State from scanner/manager    */
    /* ============================= */

    val isScanning = bleScanner.isScanning
    val foundDevices = bleScanner.devices
    val connectionState = bleManager.connectionState


    /* ============================= */
    /* Actions                       */
    /* ============================= */

    fun startScan() {
        bleScanner.start()
    }

    fun stopScan() {
        bleScanner.stop()
    }

    fun connectToDevice(address: String) {
        bleScanner.stop()
        bleManager.connect(address)
    }

    fun disconnect() {
        bleManager.disconnect()
    }
}