package com.novadownload.domain.usecase

import com.novadownload.core.engine.MediaExtractionManager
import com.novadownload.core.model.MediaInfo

class AnalyzeUrlUseCase(private val extractionManager: MediaExtractionManager) {
    suspend operator fun invoke(url: String, cookiePath: String? = null): Result<MediaInfo> {
        return extractionManager.analyze(url, cookiePath)
    }
}
