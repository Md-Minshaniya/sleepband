package com.example.sleepband.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import com.example.sleepband.core.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var bluetoothGatt: BluetoothGatt? = null

    private val _connectionState =
        MutableStateFlow<BleConnectionState>(BleConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()

    private val _rawSensorData = MutableStateFlow("")
    val rawSensorData = _rawSensorData.asStateFlow()

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _connectionState.value = BleConnectionState.Connected
                    gatt.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectionState.value = BleConnectionState.Disconnected
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) return

            val service =
                gatt.getService(UUID.fromString(Constants.SERVICE_UUID)) ?: return

            val characteristic =
                service.getCharacteristic(UUID.fromString(Constants.CHARACTERISTIC_UUID)) ?: return

            gatt.setCharacteristicNotification(characteristic, true)

            val descriptor = characteristic.getDescriptor(
                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
            )

            descriptor?.let {
                it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(it)
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            _rawSensorData.value = characteristic.getStringValue(0) ?: ""
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(deviceAddress: String) {

        disconnect()

        val manager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        val device = manager.adapter.getRemoteDevice(deviceAddress)

        _connectionState.value = BleConnectionState.Connecting

        bluetoothGatt =
            device.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        _connectionState.value = BleConnectionState.Disconnected
    }
}
