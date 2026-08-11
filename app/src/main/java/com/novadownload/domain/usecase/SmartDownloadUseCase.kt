package com.novadownload.domain.usecase

import com.novadownload.core.model.MediaFormat
import com.novadownload.core.model.MediaInfo
import com.novadownload.data.preferences.SettingsRepository

class SmartDownloadUseCase(private val settings: SettingsRepository) {
    fun selectBestFormat(mediaInfo: MediaInfo): MediaFormat? {
        val prefQuality = settings.getPreferredQuality()
        val prefFormat = settings.getPreferredFormat()
        val formats = mediaInfo.formats
        val combined = formats.filter { it.isCombined && it.ext == prefFormat }
        val bestCombined = combined.filter { (it.height ?: 0) <= prefQuality }.maxByOrNull { it.height ?: 0 }
            ?: combined.maxByOrNull { it.height ?: 0 }
        if (bestCombined != null) return bestCombined
        return formats.filter { it.height != null }.maxByOrNull { it.height!! } ?: formats.firstOrNull()
    }
}
