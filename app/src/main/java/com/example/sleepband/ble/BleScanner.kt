package com.example.sleepband.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val scanner = bluetoothAdapter?.bluetoothLeScanner

    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    private val _foundDevices = MutableStateFlow<List<ScanResult>>(emptyList())
    val foundDevices = _foundDevices.asStateFlow()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val currentList = _foundDevices.value.toMutableList()
            if (currentList.none { it.device.address == result.device.address }) {
                currentList.add(result)
                _foundDevices.value = currentList
            }
        }
    }

    fun startScanning() {
        if (_isScanning.value) return
        _foundDevices.value = emptyList()
        _isScanning.value = true
        // TODO: Add permission checks
        scanner?.startScan(scanCallback)
    }

    fun stopScanning() {
        if (!_isScanning.value) return
        _isScanning.value = false
        scanner?.stopScan(scanCallback)
    }
}
