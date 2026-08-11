package com.novadownload.core.engine

import com.novadownload.core.model.ExtractorHealth
import com.novadownload.core.model.ExtractorStatus
import com.novadownload.core.model.Platform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ExtractorHealthMonitor {
    private val _statuses = MutableStateFlow<Map<Platform, ExtractorStatus>>(emptyMap())
    val statuses: StateFlow<Map<Platform, ExtractorStatus>> = _statuses
    private val failureLog = mutableListOf<String>()

    init {
        val initial = Platform.entries.filter { it != Platform.UNKNOWN }.associateWith {
            ExtractorStatus(it, ExtractorHealth.SUPPORTED, System.currentTimeMillis())
        }
        _statuses.value = initial
    }

    fun reportSuccess(platform: Platform, extractorKey: String?) {
        val current = _statuses.value.toMutableMap()
        current[platform] = ExtractorStatus(platform, ExtractorHealth.OPERATIONAL, System.currentTimeMillis(), null, extractorKey)
        _statuses.value = current
    }

    fun reportFailure(platform: Platform, error: String) {
        val current = _statuses.value.toMutableMap()
        val health = when {
            error.contains("Unsupported URL", ignoreCase = true) -> ExtractorHealth.UNSUPPORTED_URL
            error.contains("login", ignoreCase = true) || error.contains("auth", ignoreCase = true) -> ExtractorHealth.AUTH_REQUIRED
            error.contains("region", ignoreCase = true) || error.contains("geo", ignoreCase = true) -> ExtractorHealth.REGION_RESTRICTED
            error.contains("update", ignoreCase = true) -> ExtractorHealth.REQUIRES_UPDATE
            else -> ExtractorHealth.EXTRACTION_FAILED
        }
        current[platform] = ExtractorStatus(platform, health, System.currentTimeMillis(), error)
        _statuses.value = current
        failureLog.add("${System.currentTimeMillis()} | $platform | $error")
        if (failureLog.size > 500) failureLog.removeAt(0)
    }

    fun getDiagnostics(): String = failureLog.joinToString("\n")
    fun clearLogs() { failureLog.clear() }
}
