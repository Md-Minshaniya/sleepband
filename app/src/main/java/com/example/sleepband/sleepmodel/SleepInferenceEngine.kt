package com.example.sleepband.sleepmodel

import com.example.sleepband.signal.FeatureExtractor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepInferenceEngine @Inject constructor(
    private val featureExtractor: FeatureExtractor
) : SleepModel {

    override fun predict(features: FloatArray): SleepStage {
        // TODO: Implement TFLite inference here
        // For now, return a dummy stage
        return SleepStage.N2
    }

    fun analyze(ppgData: List<Double>, motionData: List<Double>): SleepStage {
        val features = featureExtractor.extractFeatures(ppgData, motionData)
        return predict(features)
    }
}
