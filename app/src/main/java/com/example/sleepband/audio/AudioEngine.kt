package com.example.sleepband.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.sleepband.sleepmodel.SleepStage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioEngine @Inject constructor(
    private val noiseGenerator: NoiseGenerator
) {
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    private val currentStage = MutableStateFlow(SleepStage.AWAKE)

    fun setSleepStage(stage: SleepStage) {
        currentStage.value = stage
    }

    fun startPlayback(noiseType: NoiseType) {
        stopPlayback()
        
        playbackJob = scope.launch {
            val sampleRate = 44100
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_FLOAT
            )

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()

            while (playbackJob?.isActive == true) {
                val stage = currentStage.value
                val bufferSize = minBufferSize / 4
                val buffer = when (noiseType) {
                    NoiseType.PINK -> noiseGenerator.generatePinkNoise(bufferSize)
                    NoiseType.BROWN -> noiseGenerator.generateBrownNoise(bufferSize)
                    else -> FloatArray(bufferSize)
                }

                // Apply adaptive filtering and volume based on Table
                applyAdaptiveProcessing(buffer, stage)

                audioTrack?.write(buffer, 0, buffer.size, AudioTrack.WRITE_BLOCKING)
            }
        }
    }

    private fun applyAdaptiveProcessing(buffer: FloatArray, stage: SleepStage) {
        var volume: Float
        var cutoffFreq: Float 
        var highPassFreq = 20f

        when (stage) {
            SleepStage.AWAKE -> { // Awake (pre-sleep)
                volume = 0.5f     // 35-45 dB SPL
                cutoffFreq = 8000f // 20-8,000 Hz
            }
            SleepStage.N1 -> {    // Sleep onset
                volume = 0.35f    // 30-40 dB SPL
                cutoffFreq = 6000f // 20-6,000 Hz
            }
            SleepStage.N2 -> {    // Light sleep
                volume = 0.25f    // 25-35 dB SPL
                cutoffFreq = 4000f // 20-4,000 Hz
            }
            SleepStage.N3 -> {    // Deep sleep
                volume = 0.15f    // 20-30 dB SPL
                cutoffFreq = 2000f // 20-2,000 Hz
            }
            SleepStage.REM -> {   // REM sleep
                volume = 0.0f     // < 20 dB or off
                cutoffFreq = 20f
            }
            SleepStage.PRE_WAKE -> { // Pre-wake window
                volume = 0.4f     // 25 -> 40 dB ramp
                cutoffFreq = 5000f // 100-5,000 Hz
                highPassFreq = 100f
            }
            else -> {
                volume = 0.3f
                cutoffFreq = 4000f
            }
        }

        // Apply simple filtering
        filterBuffer(buffer, cutoffFreq, highPassFreq, volume)
    }

    private fun filterBuffer(buffer: FloatArray, lowPass: Float, highPass: Float, volume: Float) {
        val dt = 1.0f / 44100.0f
        
        // Low pass
        val rcLP = 1.0f / (2.0f * Math.PI.toFloat() * lowPass)
        val alphaLP = dt / (rcLP + dt)
        
        // High pass
        val rcHP = 1.0f / (2.0f * Math.PI.toFloat() * highPass)
        val alphaHP = rcHP / (rcHP + dt)

        var lastLP = 0f
        var lastHPIn = 0f
        var lastHPOut = 0f

        for (i in buffer.indices) {
            // Low pass
            val lpOut = alphaLP * buffer[i] + (1 - alphaLP) * lastLP
            lastLP = lpOut
            
            // High pass
            val hpOut = alphaHP * (lastHPOut + lpOut - lastHPIn)
            lastHPIn = lpOut
            lastHPOut = hpOut
            
            buffer[i] = hpOut * volume
        }
    }

    fun stopPlayback() {
        playbackJob?.cancel()
        playbackJob = null
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }
}
