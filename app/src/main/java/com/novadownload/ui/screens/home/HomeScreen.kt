package com.novadownload.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novadownload.core.model.MediaInfo
import com.novadownload.domain.usecase.AnalyzeUrlUseCase
import com.novadownload.core.download.DownloadManagerImpl
import com.novadownload.core.engine.EngineUpdateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val analyzeUseCase: AnalyzeUrlUseCase,
    private val downloadManager: DownloadManagerImpl,
    private val updateManager: EngineUpdateManager
) : ViewModel() {
    private val _url = MutableStateFlow("")
    val url: StateFlow<String> = _url
    private val _mediaInfo = MutableStateFlow<MediaInfo?>(null)
    val mediaInfo: StateFlow<MediaInfo?> = _mediaInfo
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun onUrlChange(newUrl: String) { _url.value = newUrl }

    fun analyze() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = analyzeUseCase(_url.value)
            result.onSuccess { _mediaInfo.value = it }
                .onFailure { _error.value = it.message }
            _loading.value = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val url by viewModel.url.collectAsState()
    val mediaInfo by viewModel.mediaInfo.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("NOVA DOWNLOAD", style = MaterialTheme.typography.headlineLarge)
        OutlinedTextField(
            value = url,
            onValueChange = viewModel::onUrlChange,
            label = { Text("Paste video URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.analyze() }, enabled = !loading) {
                Text(if (loading) "Analyzing..." else "Analyze")
            }
            OutlinedButton(onClick = {}) { Text("Paste") }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        mediaInfo?.let { info ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(info.title, style = MaterialTheme.typography.titleMedium)
                    Text("${info.platform.displayName} - ${info.uploader ?: ""}")
                    Text("Duration: ${info.durationSec ?: 0}s")
                    Text("Formats: ${info.formats.size} available")
                    info.formats.take(6).forEach { fmt ->
                        Text("${fmt.qualityLabel} - ${fmt.ext} - ${fmt.filesize ?: fmt.filesizeApprox ?: 0} bytes")
                    }
                    Button(onClick = {}) { Text("Smart Download Best") }
                }
            }
        }
    }
}
