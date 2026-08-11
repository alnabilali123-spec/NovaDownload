package com.novadownload.core.engine

import com.novadownload.core.model.MediaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class FallbackExtractor(private val cobaltInstanceUrl: String? = null) {
    private val client = OkHttpClient()

    suspend fun extract(url: String): Result<MediaInfo> = withContext(Dispatchers.IO) {
        try {
            if (cobaltInstanceUrl != null) {
                val json = JSONObject(mapOf("url" to url, "vCodec" to "h264", "vQuality" to "1080", "aFormat" to "mp3"))
                val request = Request.Builder()
                    .url(cobaltInstanceUrl)
                    .post(json.toString().toRequestBody("application/json".toMediaType()))
                    .build()
                val resp = client.newCall(request).execute()
                if (resp.isSuccessful) {
                    // Parse cobalt response would map to MediaInfo
                }
            }
            Result.failure(Exception("Fallback: no Cobalt instance configured"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
