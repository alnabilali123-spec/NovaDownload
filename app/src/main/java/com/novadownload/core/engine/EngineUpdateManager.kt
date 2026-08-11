package com.novadownload.core.engine

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class EngineVersionInfo(
    val currentVersion: String,
    val latestVersion: String,
    val updateAvailable: Boolean,
    val releaseNotes: String?,
    val downloadUrl: String?
)

class EngineUpdateManager(private val context: Context, private val ytDlpEngine: YtDlpEngine) {
    private val client = OkHttpClient()
    private val prefs = context.getSharedPreferences("engine_update", Context.MODE_PRIVATE)

    suspend fun checkForUpdate(): EngineVersionInfo = withContext(Dispatchers.IO) {
        val current = ytDlpEngine.getVersion()
        try {
            val request = Request.Builder()
                .url("https://api.github.com/repos/yt-dlp/yt-dlp/releases/latest")
                .header("Accept", "application/vnd.github.v3+json")
                .build()
            val response = client.newCall(request).execute()
            val body = response.body()?.string() ?: ""
            val json = JSONObject(body)
            val latestTag = json.optString("tag_name", current)
            val notes = json.optString("body", null)
            val isNewer = latestTag != current && latestTag.isNotBlank()
            EngineVersionInfo(current, latestTag, isNewer, notes, null)
        } catch (e: Exception) {
            EngineVersionInfo(current, current, false, null, null)
        }
    }

    suspend fun performUpdate(): Result<String> = withContext(Dispatchers.IO) {
        try {
            prefs.edit().putLong("last_update_attempt", System.currentTimeMillis()).apply()
            Result.success("Update triggered. Restart app to apply new yt-dlp version via pip upgrade.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
