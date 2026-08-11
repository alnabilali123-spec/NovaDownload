package com.novadownload.ui.screens.platforms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SupportedPlatformsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Supported Platforms", style = MaterialTheme.typography.headlineMedium)
        Text("YouTube, TikTok, Instagram, Facebook, X/Twitter, Reddit, Vimeo, Dailymotion, Twitch, Pinterest, Snapchat public, SoundCloud, Bilibili + 1800+ via yt-dlp")
        Text("Health status from ExtractorHealthMonitor: Operational / Requires Update / Auth Required etc.")
    }
}
