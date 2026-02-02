package com.example.sleepband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleepband.audio.AudioEngine
import com.example.sleepband.audio.NoiseType
import com.example.sleepband.ble.BleManager
import com.example.sleepband.sleepmodel.SleepInferenceEngine
import com.example.sleepband.sleepmodel.SleepStage
import com.example.sleepband.storage.dao.SleepSessionDao
import com.example.sleepband.storage.entity.SleepSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val bleManager: BleManager,
    private val audioEngine: AudioEngine,
    private val sleepInferenceEngine: SleepInferenceEngine,
    private val sleepSessionDao: SleepSessionDao
) : ViewModel() {

    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive = _isSessionActive.asStateFlow()

    private val _currentSleepStage = MutableStateFlow(SleepStage.UNKNOWN)
    val currentSleepStage = _currentSleepStage.asStateFlow()

    private val _ppgDataPoints = MutableStateFlow<List<Float>>(emptyList())
    val ppgDataPoints = _ppgDataPoints.asStateFlow()

    private val _motionDataPoints = MutableStateFlow<List<Float>>(emptyList())
    val motionDataPoints = _motionDataPoints.asStateFlow()

    private val _sessionHistory = MutableStateFlow<List<SleepStage>>(emptyList())
    val sessionHistory = _sessionHistory.asStateFlow()

    private var startTime: Long = 0

    init {
        viewModelScope.launch {
            bleManager.rawSensorData.collectLatest { data ->
                if (_isSessionActive.value && data.isNotEmpty()) {
                    processData(data)
                }
            }
        }
    }

    private fun processData(data: String) {
        try {
            // Data format: irValue,gx,gy,gz,vBat
            val parts = data.split(",")
            if (parts.size >= 4) {
                val irValue = parts[0].toFloatOrNull() ?: 0f
                val gx = parts[1].toFloatOrNull() ?: 0f
                val gy = parts[2].toFloatOrNull() ?: 0f
                val gz = parts[3].toFloatOrNull() ?: 0f
                
                updatePpg(irValue)
                val motionMag = sqrt((gx * gx + gy * gy + gz * gz).toDouble()).toFloat()
                updateMotion(motionMag)

                // Use the engine for prediction (resolves unused property warning)
                val stage = sleepInferenceEngine.analyze(
                    ppgData = listOf(irValue.toDouble()),
                    motionData = listOf(motionMag.toDouble())
                )
                
                _currentSleepStage.value = stage
                
                // Track stage history for dynamic hypnogram
                val currentHistory = _sessionHistory.value.toMutableList()
                currentHistory.add(stage)
                _sessionHistory.value = currentHistory
                
                updateAudio(stage)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updatePpg(value: Float) {
        val current = _ppgDataPoints.value.takeLast(100).toMutableList()
        current.add(value)
        _ppgDataPoints.value = current
    }

    private fun updateMotion(value: Float) {
        val current = _motionDataPoints.value.takeLast(100).toMutableList()
        current.add(value)
        _motionDataPoints.value = current
    }

    private fun updateAudio(stage: SleepStage) {
        audioEngine.setSleepStage(stage)
    }

    fun startSession() {
        _isSessionActive.value = true
        startTime = System.currentTimeMillis()
        _sessionHistory.value = emptyList()
        audioEngine.startPlayback(NoiseType.PINK)
        audioEngine.setSleepStage(SleepStage.AWAKE)
    }

    fun stopSession() {
        _isSessionActive.value = false
        audioEngine.stopPlayback()
        saveSession()
    }

    private fun saveSession() {
        viewModelScope.launch {
            val history = _sessionHistory.value
            val session = SleepSession(
                startTime = startTime,
                endTime = System.currentTimeMillis(),
                qualityScore = 85,
                deepSleepMinutes = history.count { it == SleepStage.N3 },
                lightSleepMinutes = history.count { it == SleepStage.N2 || it == SleepStage.N1 },
                remSleepMinutes = history.count { it == SleepStage.REM },
                awakeMinutes = history.count { it == SleepStage.AWAKE }
            )
            sleepSessionDao.insertSession(session)
        }
    }
}
