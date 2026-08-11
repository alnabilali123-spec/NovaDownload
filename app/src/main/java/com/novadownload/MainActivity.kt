package com.novadownload

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.novadownload.ui.navigation.NovaNavGraph
import com.novadownload.ui.theme.NovaDownloadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            NovaDownloadTheme {
                Scaffold(bottomBar = {
                    NavigationBar {
                        NavigationBarItem(selected = true, onClick = {}, icon = {}, label = { Text("Home") })
                        NavigationBarItem(selected = false, onClick = {}, icon = {}, label = { Text("Downloads") })
                        NavigationBarItem(selected = false, onClick = {}, icon = {}, label = { Text("Files") })
                        NavigationBarItem(selected = false, onClick = {}, icon = {}, label = { Text("History") })
                        NavigationBarItem(selected = false, onClick = {}, icon = {}, label = { Text("Settings") })
                    }
                }) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        NovaNavGraph()
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND) {
            val url = intent.getStringExtra(Intent.EXTRA_TEXT)
            // Forward to HomeViewModel via savedStateHandle - handled in NavGraph
        }
    }
}
