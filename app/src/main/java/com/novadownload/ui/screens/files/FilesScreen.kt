package com.novadownload.ui.screens.files

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilesScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Files", style = MaterialTheme.typography.headlineMedium)
        Text("Categories: Videos, Audio, Images, Other - Search, Sort, Rename, Delete, Share")
    }
}
