package com.example.sleepband.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sleepband.storage.entity.SleepSession
import com.example.sleepband.ui.theme.Orange
import com.example.sleepband.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val sessions by viewModel.allSessions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Previous Sessions", style = MaterialTheme.typography.headlineMedium, color = Orange)
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(sessions) { session ->
                SessionItem(session)
            }
        }
    }
}

@Composable
fun SessionItem(session: SleepSession) {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = sdf.format(Date(session.startTime))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(dateString, style = MaterialTheme.typography.titleMedium)
                Text("Quality Score: ${session.qualityScore}%", style = MaterialTheme.typography.bodySmall)
            }
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Orange)
        }
    }
}
