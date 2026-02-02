package com.example.sleepband.ui.device

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sleepband.ui.core.AppCard
import com.example.sleepband.ui.core.GradientBackground
import com.example.sleepband.ui.core.PrimaryPillButton

@Composable
fun DeviceConnectScreen(
    onNext: () -> Unit
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text("Sleep Health", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Connect your SleepBand device to start sessions.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BluetoothSearching,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Scan for Device", style = MaterialTheme.typography.titleLarge)
                }

                Spacer(modifier = Modifier.height(14.dp))

                PrimaryPillButton(
                    text = "Scan for Device",
                    onClick = { /* Phase 2: start scan */ }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    "Tap on a device to select",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DeviceItem("SLEEP_HEADBAND", onClick = onNext)
                    DeviceItem("SLEEP_HEADBAND_PRO", onClick = onNext)
                    DeviceItem("SLEEP_HEADBAND_PLUS", onClick = onNext)
                }
            }
        }
    }
}

@Composable
private fun DeviceItem(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 10.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
