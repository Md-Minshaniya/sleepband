package com.example.sleepband.signal

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PpgProcessor @Inject constructor() {
    fun process(rawIr: Long): Double {
        // TODO: Implement DC removal and low-pass filtering for PPG signal
        return rawIr.toDouble()
    }
}
