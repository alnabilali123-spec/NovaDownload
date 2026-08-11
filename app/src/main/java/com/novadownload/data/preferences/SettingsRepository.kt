package com.novadownload.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore(name = "nova_settings")

class SettingsRepository(private val context: Context) {
    companion object {
        val KEY_QUALITY = intPreferencesKey("preferred_quality")
        val KEY_FORMAT = stringPreferencesKey("preferred_format")
        val KEY_WIFI_ONLY = booleanPreferencesKey("wifi_only")
        val KEY_THEME = stringPreferencesKey("theme")
        val KEY_CLIPBOARD = booleanPreferencesKey("clipboard_detection")
    }

    fun getPreferredQuality(): Int = runBlocking { context.dataStore.data.first()[KEY_QUALITY] ?: 1080 }
    fun getPreferredFormat(): String = runBlocking { context.dataStore.data.first()[KEY_FORMAT] ?: "mp4" }
}
