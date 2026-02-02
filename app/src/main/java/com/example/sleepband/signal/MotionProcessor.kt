package com.example.sleepband.signal

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MotionProcessor @Inject constructor() {
    fun process(gx: Float, gy: Float, gz: Float): Double {
        // TODO: Implement movement magnitude calculation and filtering
        return Math.sqrt((gx * gx + gy * gy + gz * gz).toDouble())
    }
}
