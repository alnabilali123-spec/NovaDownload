package com.novadownload.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.novadownload.ui.screens.home.HomeScreen
import com.novadownload.ui.screens.home.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NovaNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val vm: HomeViewModel = koinViewModel()
            HomeScreen(vm)
        }
        composable("downloads") { androidx.compose.material3.Text("Downloads - WorkManager queue with pause/resume") }
        composable("files") { androidx.compose.material3.Text("Files - Categories Videos/Audio/Images") }
        composable("history") { androidx.compose.material3.Text("History - Room DB") }
        composable("settings") { androidx.compose.material3.Text("Settings - Engine version, update, theme") }
    }
}
