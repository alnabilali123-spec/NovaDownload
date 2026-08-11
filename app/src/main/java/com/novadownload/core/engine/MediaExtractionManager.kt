package com.novadownload.core.engine

import android.content.Context
import com.novadownload.core.media.PlatformDetector
import com.novadownload.core.model.MediaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaExtractionManager(
    private val context: Context,
    private val ytDlpEngine: YtDlpEngine,
    private val healthMonitor: ExtractorHealthMonitor,
    private val fallbackEngine: FallbackExtractor
) {
    suspend fun analyze(url: String, cookiePath: String? = null): Result<MediaInfo> = withContext(Dispatchers.IO) {
        if (!PlatformDetector.isValidUrl(url)) {
            return@withContext Result.failure(IllegalArgumentException("Unsupported URL: $url"))
        }
        val platform = PlatformDetector.detect(url)
        try {
            val result = ytDlpEngine.extractInfo(url, cookiePath)
            if (result.isSuccess) {
                healthMonitor.reportSuccess(platform, result.getOrNull()?.extractor)
                return@withContext result
            } else {
                healthMonitor.reportFailure(platform, result.exceptionOrNull()?.message ?: "Extraction failed")
                val fallbackResult = fallbackEngine.extract(url)
                if (fallbackResult.isSuccess) {
                    return@withContext fallbackResult
                }
                return@withContext result
            }
        } catch (e: Exception) {
            healthMonitor.reportFailure(platform, e.message ?: "Unknown")
            Result.failure(e)
        }
    }
}
