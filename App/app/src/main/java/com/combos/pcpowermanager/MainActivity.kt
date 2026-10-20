package com.combos.pcpowermanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.combos.pcpowermanager.ui.theme.PCPowerManagerTheme
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PCPowerManagerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PowerManagerScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PowerManagerScreen(modifier: Modifier = Modifier) {
    val minuteList = listOf(15, 30, 60)

    var status by remember { mutableStateOf("Idle") }
    var selectedMinutes by remember { mutableIntStateOf(0) }

    var isShutdownScheduled by remember { mutableStateOf(false) }
    var isComputerConnected by remember { mutableStateOf(false) }
    var isCheckingConnection by remember { mutableStateOf(false) }
    var isSendingTest by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val connectionStatus = when {
        isCheckingConnection -> "Checking connection..."
        isComputerConnected -> "Computer connected"
        else -> "Computer disconnected"
    }

    val shutdownButtonText = when {
        isShutdownScheduled -> "Cancel shutdown"
        selectedMinutes > 0 -> "Schedule shutdown"
        else -> "Shutdown now"
    }

    LaunchedEffect(Unit) {
        isCheckingConnection = true
        isComputerConnected = checkConnection()
        isCheckingConnection = false
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Selected: $selectedMinutes minutes")
        Text("Power Manager")
        Text(connectionStatus)
        Text("Status: $status")
        Button(
            enabled = isComputerConnected,
            onClick = {
                if (!isShutdownScheduled && selectedMinutes > 0) {
                    isShutdownScheduled = true
                    status = "Shutdown requested"
                } else if (isShutdownScheduled) {
                    isShutdownScheduled = false
                    status = "Shutdown cancelled"
                } else {
                    status = "Immediate shutdown requested"
                }
            }
        ) { Text(shutdownButtonText) }

        Button(
            enabled = !isSendingTest && isComputerConnected,
            onClick = {
                scope.launch {
                    isSendingTest = true
                    try {
//                      kotlinx.coroutines.delay(2000.milliseconds)
                        status = if (sendTestCommand()) {
                            "Server received the command"
                        } else {
                            "Failed to send command"
                        }
                    } finally {
                        isSendingTest = false
                    }
                }
            }
        ) { Text(if (isSendingTest) "Testing..." else "Test server") }

        Row {
            for (minute in minuteList) {
                TimeOptionButton(
                    minutes = minute,
                    selected = selectedMinutes == minute,
                    enabled = !isShutdownScheduled && isComputerConnected
                ) {
                    selectedMinutes = if (selectedMinutes == minute) 0 else  minute
                }
            }
        }
    }
}

@Composable
fun TimeOptionButton(
    minutes: Int,
    selected: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit
) {
    if (selected) {
        Button(
            onClick = onSelect,
            enabled = enabled
        ) { Text("$minutes min") }

    } else {
        OutlinedButton(
            onClick = onSelect,
            enabled = enabled
        ) { Text("$minutes min") }
    }
}