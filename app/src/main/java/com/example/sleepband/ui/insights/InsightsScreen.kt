package com.example.sleepband.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sleepband.sleepmodel.SleepStage
import com.example.sleepband.ui.theme.Orange
import com.example.sleepband.viewmodel.SessionViewModel

@Composable
fun InsightsScreen(
    viewModel: SessionViewModel = hiltViewModel()
) {
    val currentStage by viewModel.currentSleepStage.collectAsState()
    val ppgPoints by viewModel.ppgDataPoints.collectAsState()
    val motionPoints by viewModel.motionDataPoints.collectAsState()
    val sessionHistory by viewModel.sessionHistory.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Live Insights", style = MaterialTheme.typography.headlineMedium, color = Orange)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Current State: ${currentStage.name}", style = MaterialTheme.typography.titleLarge)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        InsightSection(title = "PPG (Raw IR)") {
            LiveGraph(points = ppgPoints, color = Color(0xFFFF5252))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        InsightSection(title = "Motion Magnitude") {
            LiveGraph(points = motionPoints, color = Color(0xFF448AFF))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        InsightSection(title = "Sleep Hypnogram (Live)") {
            DynamicHypnogram(history = sessionHistory)
        }
    }
}

@Composable
fun InsightSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            content()
        }
    }
}

@Composable
fun LiveGraph(points: List<Float>, color: Color) {
    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        if (points.isEmpty()) return@Canvas
        
        val path = Path()
        val width = size.width
        val height = size.height
        
        val maxVal = points.maxOrNull() ?: 1f
        val minVal = points.minOrNull() ?: 0f
        val range = (maxVal - minVal).coerceAtLeast(1f)

        points.forEachIndexed { index, value ->
            val x = (index.toFloat() / 100f) * width
            val y = height - ((value - minVal) / range) * height
            
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun DynamicHypnogram(history: List<SleepStage>) {
    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        if (history.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        
        val levels = mapOf(
            SleepStage.AWAKE to 0.1f,
            SleepStage.REM to 0.35f,
            SleepStage.N1 to 0.5f,
            SleepStage.N2 to 0.65f,
            SleepStage.N3 to 0.9f
        )
        
        val stepWidth = width / history.size.coerceAtLeast(1)
        
        val path = Path()
        path.moveTo(0f, height * (levels[history[0]] ?: 0.1f))
        
        for (i in 1 until history.size) {
            val prevY = height * (levels[history[i-1]] ?: 0.1f)
            val currY = height * (levels[history[i]] ?: 0.1f)
            
            path.lineTo(i * stepWidth, prevY)
            path.lineTo(i * stepWidth, currY)
        }
        
        drawPath(
            path = path,
            color = Orange,
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Grid lines
        levels.values.forEach { level ->
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(0f, height * level),
                end = Offset(width, height * level),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}
