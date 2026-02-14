package com.example.sleepband.ui.device

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sleepband.viewmodel.DeviceViewModel
import com.example.sleepband.ui.core.*

@Composable
fun DeviceConnectScreen(
    onNext: () -> Unit
) {

    val viewModel: DeviceViewModel = hiltViewModel()

    val devices by viewModel.foundDevices.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.stopScan() }
    }

    GradientBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp)
        ) {

            PrimaryPillButton(
                text = if (isScanning) "Scanning..." else "Scan for Device",
                onClick = { viewModel.startScan() }
            )

            Spacer(Modifier.height(16.dp))

            devices.forEach { result ->
                DeviceItem(
                    title = result.device.name ?: result.device.address,
                    onClick = {
                        viewModel.connectToDevice(result.device.address)
                        onNext()
                    }
                )
            }
        }
    }
}

@Composable
private fun DeviceItem(
    title: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(title)
    }
}
