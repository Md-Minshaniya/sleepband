package com.example.sleepband.ble

/**
 * Represents ONLY BLE connection states.
 *
 * Scanner state (scanning/not scanning) should be handled by BleScanner,
 * not here.
 */
sealed class BleConnectionState {

    /** No device connected */
    object Disconnected : BleConnectionState()

    /** Trying to connect */
    object Connecting : BleConnectionState()

    /** Device connected and services ready */
    object Connected : BleConnectionState()

    /** Disconnect in progress */
    object Disconnecting : BleConnectionState()

    /** Any failure */
    data class Error(val message: String) : BleConnectionState()
}
