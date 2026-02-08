package com.example.sleepband.core

import android.content.Context
import com.example.sleepband.signal.SensorData
import java.io.BufferedReader
import java.io.InputStreamReader

object CsvReader {

    fun read(context: Context): List<SensorData> {

        val list = mutableListOf<SensorData>()

        val input = context.assets.open("data.csv")
        val reader = BufferedReader(InputStreamReader(input))

        reader.readLine() // skip header

        reader.forEachLine { line ->

            val parts = line.split(",")

            // protect against bad rows
            if (parts.size < 9) return@forEachLine

            try {
                list.add(
                    SensorData(
                        timestamp = parts[0].toDouble(),  // TIMESTAMP
                        bvp = parts[1].toDouble(),  // BVP
                        accX = parts[2].toDouble(),  // ACC_X
                        accY = parts[3].toDouble(),  // ACC_Y
                        accZ = parts[4].toDouble(),  // ACC_Z
                        hr = parts[7].toInt(),     // ✅ HR
                        ibi = parts[8].toDouble()   // ✅ IBI
                    )
                )
            } catch (e: Exception) {
                // skip bad row instead of crash
            }
        }

        return list
    }
}