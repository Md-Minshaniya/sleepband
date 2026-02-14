package com.example.sleepband.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleScanner @Inject constructor(
    @ApplicationContext context: Context
) {

    private val manager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private val adapter: BluetoothAdapter? = manager.adapter
    private val scanner: BluetoothLeScanner? = adapter?.bluetoothLeScanner

    private val _devices = MutableStateFlow<List<ScanResult>>(emptyList())
    val devices = _devices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    private val callback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val currentList = _devices.value.toMutableList()

            if (currentList.none { it.device.address == result.device.address }) {
                currentList.add(result)
                _devices.value = currentList
            }
        }

        override fun onScanFailed(errorCode: Int) {
            _isScanning.value = false
        }
    }

    @SuppressLint("MissingPermission")
    fun start() {
        if (_isScanning.value) return
        if (scanner == null) return

        _devices.value = emptyList()
        _isScanning.value = true

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner.startScan(null, settings, callback)
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        if (!_isScanning.value) return
        scanner?.stopScan(callback)
        _isScanning.value = false
    }
}
