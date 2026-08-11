package com.novadownload

import android.app.Application
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.novadownload.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NovaDownloadApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NovaDownloadApp)
            modules(appModule)
        }
        try {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(this))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
