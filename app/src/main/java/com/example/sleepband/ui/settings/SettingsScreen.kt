package com.example.sleepband.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBandClick: () -> Unit
) {
    var notifications by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBg)
            .padding(18.dp)
    ) {

        Text(
            "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = Orange
        )

        Spacer(Modifier.height(24.dp))

        Text("General")

        Spacer(Modifier.height(12.dp))

        SettingRow("Profile", "Edit Account")
        Spacer(Modifier.height(10.dp))

        SettingSwitchRow("Notifications", notifications) {
            notifications = it
        }
        Spacer(Modifier.height(10.dp))

        SettingRow("Language", "English")
        Spacer(Modifier.height(10.dp))

        SettingRow("App Theme", "Light Mode")

        Spacer(Modifier.height(26.dp))

        Text("Sleep Band")

        Spacer(Modifier.height(12.dp))

        SettingRow("Band Settings", onClick = onBandClick)
    }
}
