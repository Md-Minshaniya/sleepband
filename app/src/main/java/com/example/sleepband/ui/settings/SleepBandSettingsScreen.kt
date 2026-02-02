package com.example.sleepband.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SleepBandSettingsScreen(
    onBack: () -> Unit = {}
) {

    var alerts by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(true) }
    var hrv by remember { mutableStateOf(true) }
    var spo2 by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBg)
            .padding(18.dp)
    ) {

        Row {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = Orange)
            }

            Text(
                "Sleep Band Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = Orange
            )
        }

        Spacer(Modifier.height(20.dp))

        SettingRow("Recording Duration", "8 Hours")
        Spacer(Modifier.height(10.dp))

        SettingSwitchRow("Sleep Stage Alerts", alerts) { alerts = it }
        Spacer(Modifier.height(10.dp))

        SettingSwitchRow("Vibration Alerts", vibration) { vibration = it }
        Spacer(Modifier.height(10.dp))

        SettingSwitchRow("Heart Rate Variability (HRV)", hrv) { hrv = it }
        Spacer(Modifier.height(10.dp))

        SettingSwitchRow("SpO₂ Monitoring", spo2) { spo2 = it }
        Spacer(Modifier.height(10.dp))

        SettingRow("Offline Data Storage", "Enabled")
        Spacer(Modifier.height(10.dp))

        SettingRow("Firmware Update", "Check for Updates")
    }
}
