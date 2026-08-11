package com.novadownload.ui.screens.downloads

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DownloadsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Downloads", style = MaterialTheme.typography.headlineMedium)
        Text("Active downloads with real progress, speed, pause/resume/cancel/retry via WorkManager")
    }
}
