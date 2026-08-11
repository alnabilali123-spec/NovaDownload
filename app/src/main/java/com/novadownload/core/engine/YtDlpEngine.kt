package com.novadownload.core.engine

import android.content.Context
import com.chaquo.python.Python
import com.novadownload.core.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class YtDlpEngine(private val context: Context) {

    suspend fun getVersion(): String = withContext(Dispatchers.IO) {
        try {
            val python = Python.getInstance()
            val mod = python.getModule("yt_dlp.version")
            mod.get("__version__")?.toString() ?: "unknown"
        } catch (e: Exception) {
            "2026.07.21 (bundled) - ${e.message}"
        }
    }

    suspend fun extractInfo(url: String, cookieFilePath: String? = null): Result<MediaInfo> = withContext(Dispatchers.IO) {
        try {
            val python = Python.getInstance()
            val helper = python.getModule("novadownload_extractor")
            val jsonStr = helper.callAttr("extract_info_json", url, cookieFilePath ?: "").toString()
            val parsed = parseYtDlpJson(jsonStr, url)
            Result.success(parsed)
        } catch (e: Exception) {
            Result.failure(Exception("yt-dlp extraction failed: ${e.message}", e))
        }
    }

    fun parseYtDlpJson(jsonStr: String, originalUrl: String): MediaInfo {
        val obj = JSONObject(jsonStr)
        val platform = com.novadownload.core.media.PlatformDetector.detect(originalUrl)
        val title = obj.optString("title", "Unknown Title")
        val id = obj.optString("id", java.util.UUID.randomUUID().toString())
        val uploader = obj.optString("uploader", null)
        val duration = if (obj.has("duration") && !obj.isNull("duration")) obj.optLong("duration") else null
        val thumbnail = obj.optString("thumbnail", null)
        val extractor = obj.optString("extractor_key", null)
        val isPlaylist = obj.has("entries")
        
        val formats = mutableListOf<MediaFormat>()
        val formatsArray = obj.optJSONArray("formats")
        if (formatsArray != null) {
            for (i in 0 until formatsArray.length()) {
                val f = formatsArray.getJSONObject(i)
                val formatId = f.optString("format_id", "unknown")
                val ext = f.optString("ext", "mp4")
                val height = if (f.has("height") && !f.isNull("height")) f.optInt("height") else null
                val width = if (f.has("width") && !f.isNull("width")) f.optInt("width") else null
                val vcodec = f.optString("vcodec", "none")
                val acodec = f.optString("acodec", "none")
                val isVideoOnly = vcodec != "none" && acodec == "none"
                val isAudioOnly = vcodec == "none" && acodec != "none"
                val isCombined = vcodec != "none" && acodec != "none"
                val resolution = when {
                    height != null -> "${height}p"
                    f.has("resolution") && !f.isNull("resolution") -> f.optString("resolution")
                    else -> null
                }
                val filesize = if (f.has("filesize") && !f.isNull("filesize")) f.optLong("filesize") else null
                val filesizeApprox = if (f.has("filesize_approx") && !f.isNull("filesize_approx")) f.optLong("filesize_approx") else null
                val fps = if (f.has("fps") && !f.isNull("fps")) f.optDouble("fps").toFloat() else null
                val abr = if (f.has("abr") && !f.isNull("abr")) f.optDouble("abr").toFloat() else null
                val vbr = if (f.has("vbr") && !f.isNull("vbr")) f.optDouble("vbr").toFloat() else null
                val qualityLabel = when {
                    isAudioOnly && abr != null -> "${abr.toInt()}kbps ${ext.uppercase()}"
                    height != null -> "${height}p ${ext.uppercase()} ${if (isCombined) "" else if (isVideoOnly) "(video only)" else ""}".trim()
                    else -> "${ext.uppercase()} ${formatId}"
                }
                formats.add(
                    MediaFormat(
                        formatId = formatId,
                        ext = ext,
                        resolution = resolution,
                        height = height,
                        width = width,
                        fps = fps,
                        vcodec = vcodec,
                        acodec = acodec,
                        abr = abr,
                        vbr = vbr,
                        filesize = filesize,
                        filesizeApprox = filesizeApprox,
                        isVideoOnly = isVideoOnly,
                        isAudioOnly = isAudioOnly,
                        isCombined = isCombined,
                        qualityLabel = qualityLabel
                    )
                )
            }
        }
        val sortedFormats = formats.sortedWith(compareByDescending<MediaFormat> { it.height ?: 0 }.thenByDescending { it.abr ?: 0f })

        return MediaInfo(
            id = id,
            originalUrl = originalUrl,
            platform = platform,
            title = title,
            uploader = uploader,
            durationSec = duration,
            thumbnailUrl = thumbnail,
            extractor = extractor,
            formats = sortedFormats,
            isPlaylist = isPlaylist,
            playlistCount = if (isPlaylist) obj.optJSONArray("entries")?.length() else null
        )
    }
}
