package com.novadownload.core.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaInfo(
    val id: String,
    val originalUrl: String,
    val platform: Platform,
    val title: String,
    val uploader: String? = null,
    val durationSec: Long? = null,
    val thumbnailUrl: String? = null,
    val extractor: String? = null,
    val formats: List<MediaFormat> = emptyList(),
    val subtitles: List<String> = emptyList(),
    val isPlaylist: Boolean = false,
    val playlistCount: Int? = null,
    val description: String? = null
)

@Serializable
data class MediaFormat(
    val formatId: String,
    val ext: String,
    val resolution: String?,
    val height: Int? = null,
    val width: Int? = null,
    val fps: Float? = null,
    val vcodec: String? = null,
    val acodec: String? = null,
    val abr: Float? = null,
    val vbr: Float? = null,
    val filesize: Long? = null,
    val filesizeApprox: Long? = null,
    val isVideoOnly: Boolean = false,
    val isAudioOnly: Boolean = false,
    val isCombined: Boolean = false,
    val qualityLabel: String
)

enum class ExtractorHealth {
    SUPPORTED,
    OPERATIONAL,
    TEMPORARILY_UNAVAILABLE,
    REQUIRES_UPDATE,
    UNSUPPORTED_URL,
    AUTH_REQUIRED,
    REGION_RESTRICTED,
    EXTRACTION_FAILED
}

data class ExtractorStatus(
    val platform: Platform,
    val status: ExtractorHealth,
    val lastChecked: Long,
    val lastError: String? = null,
    val ytDlpExtractorKey: String? = null
)
