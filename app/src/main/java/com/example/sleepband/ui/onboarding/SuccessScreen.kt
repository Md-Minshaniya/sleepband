package com.example.sleepband.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sleepband.ui.core.AppCard
import com.example.sleepband.ui.core.GradientBackground
import com.example.sleepband.ui.core.PrimaryPillButton

@Composable
fun SuccessScreen(
    onStart: () -> Unit
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppCard(modifier = Modifier.fillMaxWidth()) {

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text("You're ready!", style = MaterialTheme.typography.titleLarge)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Device connected successfully.\nStart your first session anytime.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(18.dp))

                PrimaryPillButton(
                    text = "Start Session",
                    onClick = onStart
                )
            }
        }
    }
}
