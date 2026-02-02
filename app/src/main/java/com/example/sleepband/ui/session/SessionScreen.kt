package com.example.sleepband.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sleepband.ui.theme.Amber
import com.example.sleepband.ui.theme.Orange
import com.example.sleepband.viewmodel.SessionViewModel

@Composable
fun SessionScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToInsights: () -> Unit,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val isActive by viewModel.isSessionActive.collectAsState()
    val sleepStage by viewModel.currentSleepStage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isActive) "Session Started" else "Welcome!",
            style = MaterialTheme.typography.headlineMedium,
            color = Orange
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isActive) "Playing Noise" else "Start Session",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = Amber.copy(alpha = 0.2f)
            ) {}
            
            IconButton(
                onClick = {
                    if (isActive) {
                        viewModel.stopSession()
                        onNavigateToHistory()
                    } else {
                        viewModel.startSession()
                    }
                },
                modifier = Modifier
                    .size(100.dp)
                    .background(Orange, CircleShape)
            ) {
                Icon(
                    imageVector = if (isActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        if (isActive) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onNavigateToInsights,
                colors = ButtonDefaults.buttonColors(containerColor = Amber),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            ) {
                Icon(Icons.Default.AutoGraph, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Live Data & Prediction", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Current Stage: ${sleepStage.name}", style = MaterialTheme.typography.bodyLarge, color = Orange)
            Text(
                "Lay comfortable and allow noise",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
