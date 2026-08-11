package com.novadownload.ui.screens.browser

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BrowserScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Internal Browser", style = MaterialTheme.typography.headlineMedium)
        Text("URL nav, back/forward/refresh, share, send URL to downloader, detect media")
    }
}
