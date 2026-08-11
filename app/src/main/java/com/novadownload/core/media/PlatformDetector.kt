package com.novadownload.core.media

import com.novadownload.core.model.Platform
import java.util.regex.Pattern

object PlatformDetector {
    private val patterns = mapOf(
        Platform.YOUTUBE to Pattern.compile("(?:youtube\\.com/(?:watch|shorts|embed)|youtu\\.be/)", Pattern.CASE_INSENSITIVE),
        Platform.TIKTOK to Pattern.compile("tiktok\\.com", Pattern.CASE_INSENSITIVE),
        Platform.INSTAGRAM to Pattern.compile("instagram\\.com/(?:p|reel|tv)/", Pattern.CASE_INSENSITIVE),
        Platform.FACEBOOK to Pattern.compile("(?:facebook\\.com|fb\\.watch)/", Pattern.CASE_INSENSITIVE),
        Platform.TWITTER to Pattern.compile("(?:twitter\\.com|x\\.com)/.+/(?:status|video)/", Pattern.CASE_INSENSITIVE),
        Platform.REDDIT to Pattern.compile("reddit\\.com", Pattern.CASE_INSENSITIVE)
    )

    fun detect(url: String): Platform {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return Platform.UNKNOWN
        patterns.forEach { (platform, pattern) ->
            if (pattern.matcher(trimmed).find()) return platform
        }
        return Platform.detect(trimmed)
    }

    fun isValidUrl(url: String): Boolean {
        return try {
            val u = java.net.URL(url.trim())
            u.protocol in listOf("http", "https") && u.host.isNotBlank()
        } catch (e: Exception) { false }
    }
}
