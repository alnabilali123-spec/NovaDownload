package com.novadownload.di

import com.novadownload.core.download.DownloadManagerImpl
import com.novadownload.core.engine.*
import com.novadownload.data.db.AppDatabase
import com.novadownload.data.preferences.SettingsRepository
import com.novadownload.domain.usecase.AnalyzeUrlUseCase
import com.novadownload.domain.usecase.SmartDownloadUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().downloadDao() }
    single { SettingsRepository(androidContext()) }
    single { YtDlpEngine(androidContext()) }
    single { ExtractorHealthMonitor() }
    single { FallbackExtractor() }
    single { MediaExtractionManager(androidContext(), get(), get(), get()) }
    single { EngineUpdateManager(androidContext(), get()) }
    single { com.novadownload.core.media.MediaProcessor(androidContext()) }
    single { DownloadManagerImpl(androidContext(), get()) }
    factory { AnalyzeUrlUseCase(get()) }
    factory { SmartDownloadUseCase(get()) }
    factory { com.novadownload.ui.screens.home.HomeViewModel(get(), get(), get()) }
}
