package com.example.sleepband.audio

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoiseGenerator @Inject constructor() {
    
    fun generatePinkNoise(bufferSize: Int): FloatArray {
        val buffer = FloatArray(bufferSize)
        // TODO: Implement actual pink noise generation algorithm (Voss-McCartney)
        for (i in 0 until bufferSize) {
            buffer[i] = (Math.random() * 2 - 1).toFloat()
        }
        return buffer
    }

    fun generateBrownNoise(bufferSize: Int): FloatArray {
        val buffer = FloatArray(bufferSize)
        // TODO: Implement brown noise (integrated white noise)
        var lastOut = 0f
        for (i in 0 until bufferSize) {
            val white = (Math.random() * 2 - 1).toFloat()
            lastOut = (lastOut + (0.02f * white)) / 1.02f
            buffer[i] = lastOut * 3.5f // rough compensation for volume
        }
        return buffer
    }
}
