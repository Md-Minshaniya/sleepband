package com.example.sleepband.sleepmodel

interface SleepModel {
    fun predict(features: FloatArray): SleepStage
}
