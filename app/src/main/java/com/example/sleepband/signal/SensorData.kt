package com.example.sleepband.signal

enum class Stage {
    WAKE, LIGHT, REM, DEEP
}

data class SensorData(
    val timestamp: Double,
    val bvp: Double,
    val accX: Double,
    val accY: Double,
    val accZ: Double,
    val hr: Int,
    val ibi: Double,

    // 🔥 new field
    var stage: Stage = Stage.LIGHT
)
