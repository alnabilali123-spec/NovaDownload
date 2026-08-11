package com.novadownload.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Text("Theme: Dark/Light")
        Text("Language: English / Arabic RTL")
        Text("Download location")
        Text("Default quality: 1080p")
        Text("Default format: mp4")
        Text("Wi-Fi only")
        Text("Clipboard detection")
        Text("Engine version: 2026.07.21")
        Button(onClick = {}) { Text("Check for engine update") }
        Text("Extractor Health Monitor - shows per-platform status")
    }
}
