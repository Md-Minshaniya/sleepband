package com.example.sleepband.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import com.example.sleepband.core.CsvReader
import com.example.sleepband.core.SleepStageDetector
import com.example.sleepband.signal.SensorData
import com.example.sleepband.signal.Stage
import com.example.sleepband.sleepmodel.SleepStage
import com.example.sleepband.ui.theme.Orange
import com.example.sleepband.viewmodel.SessionViewModel

/* ============================================================
   MAIN SCREEN
   ============================================================ */

@Composable
fun InsightsScreen(
    viewModel: SessionViewModel = hiltViewModel()
) {

    val currentStage by viewModel.currentSleepStage.collectAsState()

    /* ===============================
       CSV LOAD (background thread)
       =============================== */

    val context = LocalContext.current
    var rows by remember { mutableStateOf<List<SensorData>>(emptyList()) }

    LaunchedEffect(Unit) {
        rows = withContext(Dispatchers.IO) {
            val raw = CsvReader.read(context)
            SleepStageDetector.apply(raw)
        }
    }

    /* ===============================
       OPTIMIZED GRAPH DATA
       =============================== */

    val ppgPoints by remember(rows) {
        mutableStateOf(rows.takeLast(300).map { it.bvp.toFloat() })
    }

    val motionPoints by remember(rows) {
        mutableStateOf(rows.takeLast(300).map { it.accX.toFloat() })
    }

    val sessionHistory = emptyList<SleepStage>()

    /* ===============================
       UI
       =============================== */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text("Live Insights",
            style = MaterialTheme.typography.headlineMedium,
            color = Orange
        )

        Spacer(Modifier.height(16.dp))

        Text("Current State: ${currentStage.name}")

        Spacer(Modifier.height(24.dp))

        InsightSection("PPG (Raw IR)") {
            LiveGraph(ppgPoints, Color.Red)
        }

        Spacer(Modifier.height(24.dp))

        InsightSection("Motion Magnitude") {
            LiveGraph(motionPoints, Color.Blue)
        }

        Spacer(Modifier.height(24.dp))



        /* ===============================
           STAGE DETECTION
           =============================== */

        Spacer(Modifier.height(24.dp))

        Text(
            "Detected Sleep Stage: ${rows.lastOrNull()?.stage ?: "UNKNOWN"}",
            color = Color.Magenta
        )

        StageGraph(rows.takeLast(300))

        /* ===============================
           TABLE
           =============================== */

        Spacer(Modifier.height(32.dp))

        Text("Live Sensor Data (CSV)")
        Text("Rows loaded: ${rows.size}")

        SensorTable(rows)
    }
}


/* ============================================================
   HELPERS
   ============================================================ */

@Composable
fun InsightSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title)
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            content()
        }
    }
}

@Composable
fun LiveGraph(points: List<Float>, color: Color) {

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        if (points.isEmpty()) return@Canvas

        val path = Path()

        val width = size.width
        val height = size.height

        val max = points.maxOrNull() ?: 1f
        val min = points.minOrNull() ?: 0f
        val range = (max - min).coerceAtLeast(1f)

        points.forEachIndexed { i, v ->
            val x = i * (width / points.size)
            val y = height - ((v - min) / range) * height

            if (i == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }

        drawPath(path, color, style = Stroke(3.dp.toPx()))
    }
}

/* ============================================================
   STAGE GRAPH
   ============================================================ */

@Composable
fun StageGraph(rows: List<SensorData>) {

    val history = rows.map {
        when (it.stage) {
            Stage.WAKE -> 0f
            Stage.REM -> 1f
            Stage.LIGHT -> 2f
            Stage.DEEP -> 3f
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(8.dp)
    ) {

        if (history.isEmpty()) return@Canvas

        val step = size.width / history.size
        val h = size.height

        val path = Path()
        path.moveTo(0f, h - history[0] * 30)

        history.forEachIndexed { i, v ->
            path.lineTo(i * step, h - v * 30)
        }

        drawPath(path, Color.Magenta, style = Stroke(3.dp.toPx()))
    }
}
