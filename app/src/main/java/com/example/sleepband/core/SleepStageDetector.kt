package com.example.sleepband.core

import com.example.sleepband.signal.SensorData
import com.example.sleepband.signal.Stage
import kotlin.math.sqrt

object SleepStageDetector {

    fun detect(data: SensorData): Stage {

        val motion = sqrt(
            data.accX * data.accX +
                    data.accY * data.accY +
                    data.accZ * data.accZ
        )

        val hr = data.hr

        return when {
            motion > 40 -> Stage.WAKE
            motion < 10 && hr < 60 -> Stage.DEEP
            motion < 10 && hr > 75 -> Stage.REM
            else -> Stage.LIGHT
        }
    }

    fun apply(list: List<SensorData>): List<SensorData> {
        list.forEach {
            it.stage = detect(it)
        }
        return list
    }
}
