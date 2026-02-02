package com.example.sleepband.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sleepband.storage.entity.SleepSession
import com.example.sleepband.ui.theme.Orange
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryDetailScreen(session: SleepSession) {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = sdf.format(Date(session.startTime))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row {
                    MetricItem(label = "TIME IN BED", value = "8hr 35min")
                    Spacer(modifier = Modifier.width(24.dp))
                    MetricItem(label = "TIME ASLEEP", value = "6hr 38min")
                }
                Text(
                    text = dateString,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF448AFF), modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hypnogram Card
        Card(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                HypnogramGrid()
                DetailedHypnogram()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tabs placeholder
        Row(modifier = Modifier.fillMaxWidth()) {
            TabButton("Stages", true, Modifier.weight(1f))
            TabButton("Amounts", false, Modifier.weight(1f))
            TabButton("Comparisons", false, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Percentages", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        
        Spacer(modifier = Modifier.height(16.dp))

        PercentageItem("Average Awake", "3%", Color(0xFFFF8A65))
        PercentageItem("Average REM", "25%", Color(0xFF4FC3F7))
        PercentageItem("Average Core", "58%", Color(0xFF448AFF))
        PercentageItem("Average Deep", "14%", Color(0xFF3F51B5))
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Column {
        Text(label, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TabButton(label: String, selected: Boolean, modifier: Modifier) {
    Surface(
        modifier = modifier.height(40.dp),
        color = if (selected) Color.White else Color(0xFFE0E0E0),
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, color = if (selected) Color.Black else Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun PercentageItem(label: String, value: String, color: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Text(label, color = Color.Black, fontWeight = FontWeight.Medium)
            }
            Text(value, color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HypnogramGrid() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val levels = listOf(0.1f, 0.35f, 0.65f, 0.9f)
        val labels = listOf("Awake", "REM", "Core", "Deep")
        
        levels.forEachIndexed { index, level ->
            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(0f, height * level),
                end = Offset(width, height * level),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@Composable
fun DetailedHypnogram() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Mocking the complex path from the image
        val path = Path()
        path.moveTo(0f, height * 0.65f) // Start at Core
        
        // Simplified representation of the image path
        path.lineTo(width * 0.1f, height * 0.65f)
        path.lineTo(width * 0.1f, height * 0.9f)  // Drop to Deep
        path.lineTo(width * 0.15f, height * 0.9f)
        path.lineTo(width * 0.15f, height * 0.65f)
        path.lineTo(width * 0.25f, height * 0.65f)
        path.lineTo(width * 0.25f, height * 0.1f)  // Jump to Awake
        path.lineTo(width * 0.3f, height * 0.1f)
        path.lineTo(width * 0.3f, height * 0.65f)
        
        drawPath(
            path = path,
            color = Color(0xFF448AFF),
            style = Stroke(width = 4.dp.toPx())
        )
    }
}
